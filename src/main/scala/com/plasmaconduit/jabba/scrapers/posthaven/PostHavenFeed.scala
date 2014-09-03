package com.plasmaconduit.jabba.scrapers.posthaven

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object PostHavenFeed {

  val machine = ScraperStateMachine(
    name    = "PostHaven_Feed",
    pending = PendingScraper(
      initialUrls = URLs(
        "http://blog.samaltman.com/",
        "http://blog.garrytan.com/",
        "http://alexisohanian.com/",
        "http://blog.harjtaggar.com/",
        "http://blog.tlb.org/",
        "http://www.aaronkharris.com/",
        "http://blog.ycombinator.com/",
        "http://blog.posthaven.com/"
      )
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = ".post-title a",
        nodeTarget = PostHavenNode(),
        nextLink   = ".pagination a[rel='next']",
        nextTarget = PostHavenFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(PostHavenNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
