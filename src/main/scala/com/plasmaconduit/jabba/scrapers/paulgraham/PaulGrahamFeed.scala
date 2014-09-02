package com.plasmaconduit.jabba.scrapers.paulgraham

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PaulGrahamFeed {

  val machine = ScraperStateMachine(
    "PaulGraham_Feed",
    PendingScraper(
      initialUrls = Url.fromVector(Vector("http://www.paulgraham.com/articles.html"))
    ),
    RunningScraper(
      sleep  = 30.minutes,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: Url, page: DomRoot): ScraperResult = {
    val essays = page
      .querySelectorAll("font a")
      .dropRight(1)
      .flatMap(n => n.getAttribute("href"))
      .flatMap(n => Url.parseFull(n))
      .map(ScraperTarget(PaulGrahamNode(), _))
    ScraperResult(url, None, essays, machine)
  }

}
