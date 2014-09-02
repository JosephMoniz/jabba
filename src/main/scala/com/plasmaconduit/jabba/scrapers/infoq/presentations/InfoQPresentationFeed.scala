package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQPresentationFeed {

  val machine = ScraperStateMachine(
    "InfoQ_Presentation_Feed",
    PendingScraper(
      initialUrls = Url.fromVector(Vector("http://www.infoq.com/presentations/"))
    ),
    RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: Url, page: DomRoot): ScraperResult = {
    val targets      = scrapePresentationLinks(url, page)
    val nextPage     = scrapeNextLink(url, page)
    val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperResult(url, None, allTargets, machine.toState(scraperState))
  }

  def scrapePresentationLinks(base: Url, document: DomRoot): Vector[ScraperTarget] = for (
    link      <- document.querySelectorAll(".news_type_video h2 a");
    url       <- link.getAttribute("href").toVector;
    canonical <- Url.canonicalize(base, url).toVector
  ) yield ScraperTarget(InfoQPresentationNode(), canonical)

  def scrapeNextLink(base: Url, document: DomRoot): Option[ScraperTarget] = for (
    link      <- document.querySelectorAll(".load_more_articles a.blue").lastOption;
    target    <- link.getAttribute("href");
    canonical <- Url.canonicalize(base, target)
  ) yield ScraperTarget(InfoQPresentationFeed(), canonical)

}
