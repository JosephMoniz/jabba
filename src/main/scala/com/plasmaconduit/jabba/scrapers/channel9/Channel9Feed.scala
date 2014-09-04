package com.plasmaconduit.jabba.scrapers.channel9

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object Channel9Feed {

  val machine = ScraperStateMachine(
    name       = "Channel9_Feed",
    pending    = PendingScraper(
      initialUrls = URLs("http://channel9.msdn.com/Browse")
    ),
    running    = RunningScraper(
      sleep  = 150.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = "a.title",
        nodeTarget = Channel9Node(),
        nextLink   = ".next a",
        nextTarget = Channel9Feed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(Channel9Node())
    )
  )

  def apply(): ScraperStateMachine = machine

}
