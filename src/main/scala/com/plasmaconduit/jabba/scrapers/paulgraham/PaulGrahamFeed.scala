package com.plasmaconduit.jabba.scrapers.paulgraham

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PaulGrahamFeed {

  val machine = ScraperStateMachine(
    "PaulGraham_Feed",
    PendingScraper(initialUrls = Vector("http://www.paulgraham.com/articles.html")),
    RunningScraper(
      sleep  = 30.minutes,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: String, page: DomRoot): ScraperResult = {
    val essays = page
      .querySelectorAll("font a")
      .dropRight(1)
      .flatMap(_.getAttribute("href"))
      .map(ScraperTarget(PaulGrahamNode(), _))
    ScraperResult(url, None, essays, machine)
  }

}
