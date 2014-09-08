package com.plasmaconduit.jabba.scrapers.svbtle

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
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
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes(".article_title a"),
        nodeTarget = SvbtleNode(),
        nextLinks  = RelNextLink(),
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
