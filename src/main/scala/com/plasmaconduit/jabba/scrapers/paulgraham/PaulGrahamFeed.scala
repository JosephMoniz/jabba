package com.plasmaconduit.jabba.scrapers.paulgraham

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PaulGrahamFeed {

  val machine = ScraperStateMachine(
    name    = "PaulGraham_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.paulgraham.com/articles.html")
    ),
    running = RunningScraper(
      sleep  = 30.minutes,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(PaulGrahamNode())
    )
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, page: DomRoot): ScraperResult = {
    val essays = page
      .querySelectorAll("font a")
      .dropRight(1)
      .flatMap(n => n.getAttribute("href"))
      .flatMap(n => URL.parseFull(n))
      .map(ScraperTarget(PaulGrahamNode(), _))
    ScraperSuccess(url, None, essays, machine)
  }

}
