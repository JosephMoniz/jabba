package com.plasmaconduit.jabba.scrapers.bhorowitz

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import scala.concurrent.duration._

object BHorowitzFeed {

  val machine = ScraperStateMachine(
    name    = "BenHorowitz_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.bhorowitz.com/")
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes("h3 a"),
        nodeTarget = BHorowitzNode(),
        nextLinks  = LastNode(CssSelectorNodes( ".pagination a")),
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
