package com.plasmaconduit.jabba.scrapers.theverge

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object TheVergeFeed {

  val machine = ScraperStateMachine(
    name = "TheVerge_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.theverge.com/archives")
    ),
    running = RunningScraper(
     sleep  = 150.seconds,
     scrape = FeedScraper(
       nodeLinks  = CssSelectorNodes(".body h3 a"),
       nodeTarget = TheVergeNode(),
       nextLinks  = RelNextLink(),
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
