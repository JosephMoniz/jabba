package com.plasmaconduit.jabba.scrapers.posthaven

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PostHavenFeed {

  val machine = ScraperStateMachine(
    name    = "PostHaven_Feed",
    pending = PendingScraper(
      initialUrls = URLs(
        "http://blog.samaltman.com/",
        "http://blog.garrytan.com/",
        "http://alexisohanian.com/",
        "http://blog.harjtaggar.com/",
        "http://blog.tlb.org/",
        "http://www.aaronkharris.com/",
        "http://blog.ycombinator.com/"
      )
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(PostHavenNode())
    )
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, document: DomRoot): ScraperResult = {
    val targets      = scrapeArticleLinks(url, document)
    val nextPage     = scrapeNextLink(url, document)
    val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
    ScraperSuccess(url, None, allTargets, machine)
  }

  def scrapeArticleLinks(base: URL, document: DomRoot): Vector[ScraperTarget] = for (
    link      <- document.querySelectorAll(".post-title a");
    url       <- link.getAttribute("href").toVector;
    canonical <- URL.canonicalize(base, url).toVector
  ) yield ScraperTarget(PostHavenNode(), canonical)

  def scrapeNextLink(base: URL, document: DomRoot): Option[ScraperTarget] = for (
    link      <- document.querySelectorAll(".pagination a[rel='next']").lastOption;
    target    <- link.getAttribute("href");
    canonical <- URL.canonicalize(base, target)
  ) yield ScraperTarget(PostHavenFeed(), canonical)

}
