package com.plasmaconduit.jabba.scrapers.svbtle

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object SvbtleFeed {

  val machine = ScraperStateMachine(
    name    = "Svbtle_Feed",
    pending = PendingScraper(
      initialUrls = URLs(
        "http://500hats.com/",
        "http://justinkan.com/",
        "http://daltoncaldwell.com/"
      )
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = ".article_title a",
        nodeTarget = SvbtleNode(),
        nextLink   = ".next a[rel='next']",
        nextTarget = SvbtleFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(SvbtleNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
