package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import scala.concurrent.duration._


object TechCrunchFeed {

  val machine = ScraperStateMachine(
    name    = "TechCrunch_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.techcrunch.com/")
    ),
    running = RunningScraper(
      sleep  = 300.seconds,
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes(".river-block h2 a"),
        nodeTarget = TechCrunchNode(),
        nextLinks  = LastNode(CssSelectorNodes(".pagination li a")),
        nextTarget = TechCrunchFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(TechCrunchNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
