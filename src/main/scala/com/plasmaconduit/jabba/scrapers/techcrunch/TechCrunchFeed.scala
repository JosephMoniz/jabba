package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._


case object TechCrunchFeed {

  val machine = ScraperStateMachine(
    name    = "TechCrunch_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.techcrunch.com/")
    ),
    running = RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(TechCrunchNode())
    )
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, document: DomRoot): ScraperResult = {
    val targets      = scrapeArticleLinks(url, document)
    val nextPage     = scrapeNextLink(url, document)
    val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperSuccess(url, None, allTargets, machine.toState(scraperState))
  }

  def scrapeArticleLinks(base: URL, document: DomRoot): Vector[ScraperTarget] = for (
    link      <- document.querySelectorAll(".river-block h2 a");
    url       <- link.getAttribute("href").toVector;
    canonical <- URL.canonicalize(base, url).toVector
  ) yield ScraperTarget(TechCrunchNode(), canonical)

  def scrapeNextLink(base: URL, document: DomRoot): Option[ScraperTarget] = for (
    link      <- document.querySelectorAll(".pagination li a").lastOption;
    target    <- link.getAttribute("href");
    canonical <- URL.canonicalize(base, target)
  ) yield ScraperTarget(TechCrunchFeed(), canonical)

}
