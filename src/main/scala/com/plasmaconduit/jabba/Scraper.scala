package com.plasmaconduit.jabba

import java.util.Date
import com.plasmaconduit.jabba.browsers.dom._
import rx.lang.scala._
import scala.collection.immutable._
import scala.concurrent.duration._

sealed trait Scraper {
  val state: ScraperActivityState
}

case class PendingScraper(initialUrls: Vector[String] = Vector(),
                          dependencies: Vector[Scraper] = Vector()) extends Scraper
{
  val state: ScraperActivityState = Pending
}

case class RunningScraper(sleep: Duration,
                          scrape: (ScraperStateMachine, String, DomRoot) => ScraperResult) extends Scraper
{
  val state: ScraperActivityState = Running
}

case class CompletedScraper() extends Scraper {
  val state: ScraperActivityState = Completed
}

sealed trait ScraperActivityState
case object Pending extends ScraperActivityState
case object Running extends ScraperActivityState
case object Completed extends ScraperActivityState

class ScraperStateMachine(val name: String,
                          val pending: PendingScraper,
                          val running: RunningScraper,
                          val completed: CompletedScraper,
                          val lastRun: Long,
                          val current: Scraper)
{

  def toState(state: ScraperActivityState): ScraperStateMachine = {
    new ScraperStateMachine(name, pending, running, completed, lastRun, state match {
      case Pending   => pending
      case Running   => running
      case Completed => completed
    })
  }

  def isReady: Boolean = current match {
    case PendingScraper(_, _) => true
    case RunningScraper(s, _) => new Date().getTime >= (lastRun + s.toMillis)
    case CompletedScraper()   => false
  }

  def updateLastRan(): ScraperStateMachine = current.state match {
    case Running => new ScraperStateMachine(name, pending, running, completed, new Date().getTime, running)
    case _       => this
  }

}

object ScraperStateMachine {

  def apply(n: String, p: PendingScraper, r: RunningScraper, c: CompletedScraper) = {
    new ScraperStateMachine(n, p, r, c, new Date().getTime, p)
  }

  def unapply(m: ScraperStateMachine) = Some((m.name, m.pending, m.running, m.completed, m.current))

}

case class ScraperTarget(scraper: ScraperStateMachine, url: String) {

  def toLedgerAdditionEntry: ScraperTargetAdditionEntry = {
    ScraperTargetAdditionEntry(new Date().getTime, ScraperTargetAttribute(scraper.name, url))
  }

}

case class ScraperResult(url: String,
                         data: Option[Map[String, String]],
                         targets: Vector[ScraperTarget],
                         scraper: ScraperStateMachine)
{

  def toLedgerEntry: ScraperResultEntry = ScraperResultEntry(
    timestamp = new Date().getTime,
    scraped   = ScraperTargetAttribute(scraper.name, url),
    state     = ScraperStateChangeAttribute(scraper.name, scraper.current.state),
    data      = data,
    targets   = targets.map(n => ScraperTargetAttribute(n.scraper.name, n.url))
  )

}

case class ScraperState(stateMachine: ScraperStateMachine,
                        queue: Vector[String] = Vector(),
                        done:  Vector[String] = Vector())
{

  def addUrlToQueue(url: String): ScraperState = {
    copy(queue = queue :+ url)
  }

  def removeUrlFromQueue(url: String): ScraperState = {
    copy(queue = queue.filter(_ != url))
  }

  def nextUrlFromQueue: Observable[String] = queue.headOption match {
    case None    => Observable.empty
    case Some(n) => Observable.just(n)
  }

  def addUrlToDone(url: String): ScraperState = {
    copy(done = done :+ url)
  }

  def markUrlAsScraped(url: String): ScraperState = {
    copy(stateMachine = stateMachine.updateLastRan())
      .removeUrlFromQueue(url)
      .addUrlToDone(url)
  }

  def transitionState(state: ScraperActivityState): ScraperState = {
    copy(stateMachine = stateMachine.toState(state))
  }

}

case class Scrapers(scrapers: Map[String, ScraperState] = HashMap()) {

  def addTargetToQueue(t: ScraperTargetAttribute): Scrapers = {
    scrapers
      .get(t.scraper)
      .map({ n =>
        Scrapers(scrapers + (t.scraper -> n.addUrlToQueue(t.url)))
      })
      .getOrElse(this)
  }

  def addMultipleTargetsToQueue(ts: Vector[ScraperTargetAttribute]): Scrapers = {
    ts.foldLeft(this)((m, t) => m.addTargetToQueue(t))
  }

  def removeTargetFromQueue(t: ScraperTargetAttribute): Scrapers = {
    scrapers
      .get(t.scraper)
      .map(n => Scrapers(scrapers + (t.scraper -> n.removeUrlFromQueue(t.url))))
      .getOrElse(this)
  }

  def addTargetToDone(t: ScraperTargetAttribute): Scrapers = {
    scrapers
      .get(t.scraper)
      .map(n => Scrapers(scrapers + (t.scraper -> n.addUrlToDone(t.url))))
      .getOrElse(this)
  }

  def markTargetAsScraped(t: ScraperTargetAttribute): Scrapers = {
    scrapers
      .get(t.scraper)
      .map(n => Scrapers(scrapers + (t.scraper -> n.markUrlAsScraped(t.url))))
      .getOrElse(this)
  }

  def transitionScraperState(s: ScraperStateChangeAttribute) = {
    scrapers
    .get(s.scraper)
    .map(n => Scrapers(scrapers + (s.scraper -> n.transitionState(s.state))))
    .getOrElse(this)
  }

  def readyScrapers: Observable[ScraperState] = {
    Observable.from(scrapers.values.filter(m => m.stateMachine.isReady))
  }

  def initialUrls: Observable[ScraperTarget] = {
    Observable.from(scrapers.values.flatMap(n =>
      n.stateMachine.pending.initialUrls.map(u =>
        ScraperTarget(n.stateMachine, u))
    ))
  }

}

object Scrapers {

  def fromStateMachineVector(scrapers: Vector[ScraperStateMachine]): Scrapers = {
    scrapers.foldLeft(Scrapers()) {(m, n) =>
      m.copy(scrapers = m.scrapers + (n.name -> ScraperState(n)))
    }
  }

}