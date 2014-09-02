package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._


case object TechCrunchFeed {

  val machine = ScraperStateMachine(
    "TechCrunch_Feed",
    PendingScraper(initialUrls = Url.parseFull("http://www.techcrunch.com/").toVector),
    RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: Url, page: DomRoot): ScraperResult = {
    val targets      = scrapeArticleLinks(url, page)
    val nextPage     = scrapeNextLink(url, page)
    val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperResult(url, None, allTargets, machine.toState(scraperState))
  }

  def scrapeArticleLinks(base: Url, page: DomRoot): Vector[ScraperTarget] = for (
    link      <- page.querySelectorAll(".river-block h2 a");
    url       <- link.getAttribute("href").toVector;
    canonical <- Url.canonicalize(base, url).toVector
  ) yield ScraperTarget(TechCrunchNode(), canonical)

  def scrapeNextLink(base: Url, page: DomRoot): Option[ScraperTarget] = for (
    link      <- page.querySelectorAll(".pagination li a").lastOption;
    target    <- link.getAttribute("href");
    canonical <- Url.canonicalize(base, target)
  ) yield ScraperTarget(TechCrunchFeed(), canonical)

}
