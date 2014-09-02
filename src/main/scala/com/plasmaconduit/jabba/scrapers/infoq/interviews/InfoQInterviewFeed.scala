package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQInterviewFeed {

  val machine = ScraperStateMachine(
    "InfoQ_Interview_Feed",
    PendingScraper(initialUrls = Vector("http://www.infoq.com/interviews/")),
    RunningScraper(
      sleep  = 30.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

   def apply(): ScraperStateMachine = machine

   def scrape(machine: ScraperStateMachine, url: String, page: DomRoot): ScraperResult = {
     val targets      = scrapeInterviewLinks(page)
     val nextPage     = scrapeNextLink(page)
     val scraperState = nextPage.map(_ => Running).getOrElse(Completed)
     val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
     ScraperResult(url, None, allTargets, machine.toState(scraperState))
   }

   def scrapeInterviewLinks(page: DomRoot): Vector[ScraperTarget] = for (
     link <- page.querySelectorAll(".news_type_video h2 a");
     url  <- link.getAttribute("href").toVector
   ) yield ScraperTarget(InfoQInterviewNode(), url)

   def scrapeNextLink(page: DomRoot): Option[ScraperTarget] = for (
     link   <- page.querySelectorAll(".load_more_articles a.blue").lastOption;
     target <- link.getAttribute("href")
   ) yield ScraperTarget(InfoQInterviewFeed(), target)

 }
