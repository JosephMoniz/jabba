package com.plasmaconduit.jabba.scrapers.gigaom

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common.SimpleFeedScraper
import scala.concurrent.duration._

object GigaOmFeed {

  val machine = ScraperStateMachine(
    name = "GigaOm_Feed",
    pending = PendingScraper(
      initialUrls = URLs("https://gigaom.com/")
    ),
    running = RunningScraper(
      sleep  = 600.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = ".entry-title a",
        nodeTarget = GigaOmNode(),
        nextLink   = "a.next",
        nextTarget = GigaOmFeed()
      )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(GigaOmNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
