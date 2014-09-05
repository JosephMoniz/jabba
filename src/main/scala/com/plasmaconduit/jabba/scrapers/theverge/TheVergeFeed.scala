package com.plasmaconduit.jabba.scrapers.theverge

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common.SimpleFeedScraper
import scala.concurrent.duration._

object TheVergeFeed {

  val machine = ScraperStateMachine(
    name = "TheVerge_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.theverge.com/archives")
    ),
    running = RunningScraper(
     sleep  = 150.seconds,
     scrape = SimpleFeedScraper(
       nodeLinks  = ".body h3 a",
       nodeTarget = TheVergeNode(),
       nextLink   = "a[rel='next']",
       nextTarget = TheVergeFeed()
     )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(TheVergeNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
