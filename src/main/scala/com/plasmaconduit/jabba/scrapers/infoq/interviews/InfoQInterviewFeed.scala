package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQInterviewFeed {

  val machine = ScraperStateMachine(
    "InfoQ_Interview_Feed",
    PendingScraper(
      initialUrls = Url.fromVector(Vector("http://www.infoq.com/interviews/"))
    ),
    RunningScraper(
      sleep  = 120.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

   def apply(): ScraperStateMachine = machine

   def scrape(machine: ScraperStateMachine, url: Url, page: DomRoot): ScraperResult = {
     val targets      = scrapeInterviewLinks(url, page)
     val nextPage     = scrapeNextLink(url, page)
     val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
     val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
     ScraperResult(url, None, allTargets, machine.toState(scraperState))
   }

   def scrapeInterviewLinks(base: Url, page: DomRoot): Vector[ScraperTarget] = for (
     link      <- page.querySelectorAll(".news_type_video h2 a");
     url       <- link.getAttribute("href").toVector;
     canonical <- Url.canonicalize(base, url).toVector
   ) yield ScraperTarget(InfoQInterviewNode(), canonical)

   def scrapeNextLink(base: Url, page: DomRoot): Option[ScraperTarget] = for (
     link      <- page.querySelectorAll(".load_more_articles a.blue").lastOption;
     target    <- link.getAttribute("href");
     canonical <- Url.canonicalize(base, target)
   ) yield ScraperTarget(InfoQInterviewFeed(), canonical)

 }
