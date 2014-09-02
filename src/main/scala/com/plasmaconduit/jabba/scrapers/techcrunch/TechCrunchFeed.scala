package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._


case object TechCrunchFeed {

  val machine = ScraperStateMachine(
    "TechCrunch_Feed",
    PendingScraper(initialUrls = Vector("http://www.techcrunch.com/")),
    RunningScraper(
      sleep  = 130.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: String, page: DomRoot): ScraperResult = {
    val targets      = scrapeArticleLinks(page)
    val nextPage     = scrapeNextLink(page)
    val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperResult(url, None, allTargets, machine.toState(scraperState))
  }

  def scrapeArticleLinks(page: DomRoot): Vector[ScraperTarget] = for (
    link <- page.querySelectorAll(".river-block h2 a");
    url  <- link.getAttribute("href").toVector
  ) yield ScraperTarget(TechCrunchNode(), url)

  def scrapeNextLink(page: DomRoot): Option[ScraperTarget] = for (
    link   <- page.querySelectorAll(".pagination li a").lastOption;
    target <- link.getAttribute("href")
  ) yield ScraperTarget(TechCrunchFeed(), target)

}
