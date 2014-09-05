package com.plasmaconduit.jabba.scrapers.bhorowitz

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object BHorowitzFeed {

  val machine = ScraperStateMachine(
    name    = "BenHorowitz_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.bhorowitz.com/")
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = "h3 a",
        nodeTarget = BHorowitzNode(),
        nextLink   = ".pagination a",
        nextTarget = BHorowitzFeed()
      )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(BHorowitzNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
