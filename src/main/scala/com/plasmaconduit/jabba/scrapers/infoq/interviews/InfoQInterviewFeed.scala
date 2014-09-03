package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQInterviewFeed {

  val machine = ScraperStateMachine(
    name    = "InfoQ_Interview_Feed",
    pending = PendingScraper(
      initialUrls = URL.fromVector(Vector("http://www.infoq.com/interviews/"))
    ),
    running = RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(InfoQInterviewNode())
    )
  )

   def apply(): ScraperStateMachine = machine

   def scrape(machine: ScraperStateMachine, url: URL, page: DomRoot): ScraperResult = {
     val targets      = scrapeInterviewLinks(url, page)
     val nextPage     = scrapeNextLink(url, page)
     val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
     val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
     ScraperSuccess(url, None, allTargets, machine.toState(scraperState))
   }

   def scrapeInterviewLinks(base: URL, page: DomRoot): Vector[ScraperTarget] = for (
     link      <- page.querySelectorAll(".news_type_video h2 a");
     url       <- link.getAttribute("href").toVector;
     canonical <- URL.canonicalize(base, url).toVector
   ) yield ScraperTarget(InfoQInterviewNode(), canonical)

   def scrapeNextLink(base: URL, page: DomRoot): Option[ScraperTarget] = for (
     link      <- page.querySelectorAll(".load_more_articles a.blue").lastOption;
     target    <- link.getAttribute("href");
     canonical <- URL.canonicalize(base, target)
   ) yield ScraperTarget(InfoQInterviewFeed(), canonical)

 }
