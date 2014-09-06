package com.plasmaconduit.jabba.scrapers.gigaom

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object GigaOmFeed {

  val machine = ScraperStateMachine(
    name = "GigaOm_Feed",
    pending = PendingScraper(
      initialUrls = URLs("https://gigaom.com/")
    ),
    running = RunningScraper(
      sleep  = 600.seconds,
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes(".entry-title a"),
        nodeTarget = GigaOmNode(),
        nextLinks  = LastNode(CssSelectorNodes("a.next")),
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
