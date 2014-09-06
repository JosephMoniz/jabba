package com.plasmaconduit.jabba.scrapers.a16z

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object A16zFeed {

    val machine = ScraperStateMachine(
    name = "A16z_Feed",
    pending = PendingScraper(
      initialUrls = URLs(
        "http://a16z.com/",
        "http://john.a16z.com/",
        "http://jeff.a16z.com/",
        "http://peter.a16z.com/",
        "http://scott.a16z.com/"
      )
    ),
    running = RunningScraper(
      sleep = 150.seconds,
      scrape = FeedScraper(
        nodeLinks  = DatedLinks(CssSelectorNodes("a")),
        nodeTarget = A16zNode(),
        nextLinks  = CssSelectorNodes(".nav-previous a"),
        nextTarget = A16zFeed()
      )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(A16zNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
