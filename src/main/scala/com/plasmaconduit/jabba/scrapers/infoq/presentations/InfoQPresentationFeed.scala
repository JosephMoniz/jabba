package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQPresentationFeed {

  val machine = ScraperStateMachine(
    "InfoQ_Presentation_Feed",
    PendingScraper(initialUrls = Vector("http://www.infoq.com/presentations/")),
    RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: String, page: DomRoot): ScraperResult = {
    val targets      = scrapePresentationLinks(page)
    val nextPage     = scrapeNextLink(page)
    val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperResult(url, None, allTargets, machine.toState(scraperState))
  }

  def scrapePresentationLinks(page: DomRoot): Vector[ScraperTarget] = for (
    link <- page.querySelectorAll(".news_type_video h2 a");
    url  <- link.getAttribute("href").toVector
  ) yield ScraperTarget(InfoQPresentationNode(), url)

  def scrapeNextLink(page: DomRoot): Option[ScraperTarget] = for (
    link   <- page.querySelectorAll(".load_more_articles a.blue").lastOption;
    target <- link.getAttribute("href")
  ) yield ScraperTarget(InfoQPresentationFeed(), target)

}
