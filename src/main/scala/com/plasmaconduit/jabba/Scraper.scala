package com.plasmaconduit.jabba

import java.util.Date
import com.plasmaconduit.jabba.browsers.dom._
import rx.lang.scala._
import scala.collection.immutable._
import scala.concurrent.duration._

sealed trait Scraper {
  val state: ScraperActivityState
}

case class PendingScraper(initialUrls: Vector[URL] = Vector(),
                          dependencies: Vector[Scraper] = Vector()) extends Scraper
{
  val state: ScraperActivityState = Pending
}

case class RunningScraper(sleep: Duration,
                          scrape: (ScraperStateMachine, URL, DomRoot) => ScraperResult) extends Scraper
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

case class ScraperStateMachine(name: String,
                               pending: PendingScraper,
                               running: RunningScraper,
                               completed: CompletedScraper,
                               assertions: ScraperAssertion,
                               lastRun: Long,
                               current: Scraper)
{

  def toState(state: ScraperActivityState): ScraperStateMachine = {
    copy(current = state match {
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
    case Running => copy(lastRun = new Date().getTime)
    case _       => this
  }

}

object ScraperStateMachine {

  def apply(name: String,
            pending: PendingScraper,
            running: RunningScraper,
            completed: CompletedScraper,
            assertions: ScraperAssertion = NilScraperAssertion): ScraperStateMachine =
  {
    ScraperStateMachine(name, pending, running, completed, assertions, new Date().getTime, pending)
  }

}

case class ScraperTarget(scraper: ScraperStateMachine, url: URL) {

  def toLedgerAdditionEntry: ScraperTargetAdditionEntry = {
    ScraperTargetAdditionEntry(new Date().getTime, ScraperTargetAttribute(scraper.name, url))
  }

}

sealed trait ScraperResult {
  def toLedgerEntry: LedgerEntry
}

case class ScraperSuccess(url: URL,
                          data: Option[Map[String, String]],
                          targets: Vector[ScraperTarget],
                          scraper: ScraperStateMachine) extends ScraperResult
{

  def toLedgerEntry: LedgerEntry = ScraperSuccessEntry(
    timestamp = new Date().getTime,
    scraped   = ScraperTargetAttribute(scraper.name, url),
    state     = ScraperStateChangeAttribute(scraper.name, scraper.current.state),
    data      = data,
    targets   = targets.map(n => ScraperTargetAttribute(n.scraper.name, n.url))
  )

}

case class ScraperFailure(url: URL,
                          scraper: ScraperStateMachine,
                          reason: String) extends ScraperResult
{
  def toLedgerEntry: LedgerEntry = ScraperFailureEntry(
    timestamp = new Date().getTime,
    scraped   = ScraperTargetAttribute(scraper.name, url),
    reason    = reason
  )
}

case class ScraperState(stateMachine: ScraperStateMachine,
                        queue: Vector[URL] = Vector(),
                        done:  Vector[URL] = Vector())
{

  def addUrlToQueue(url: URL): ScraperState = {
    copy(queue = queue :+ url)
  }

  def removeUrlFromQueue(url: URL): ScraperState = {
    copy(queue = queue.filter(_ != url))
  }

  def nextUrlFromQueue: Observable[URL] = queue.headOption match {
    case None    => Observable.empty
    case Some(n) => Observable.just(n)
  }

  def addUrlToDone(url: URL): ScraperState = {
    copy(done = done :+ url)
  }

  def markUrlAsScraped(url: URL): ScraperState = {
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