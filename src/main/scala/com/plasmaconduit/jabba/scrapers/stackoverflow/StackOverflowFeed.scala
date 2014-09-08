package com.plasmaconduit.jabba.scrapers.stackoverflow

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import scala.concurrent.duration._

object StackOverflowFeed {

  val machine = ScraperStateMachine(
    name = "StackOverflow_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://stackoverflow.com/questions/")
    ),
    running = RunningScraper(
      sleep  = 225.seconds,
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes("a.question-hyperlink"),
        nodeTarget = StackOverflowNode(),
        nextLinks  = RelNextLink(),
        nextTarget = StackOverflowFeed()
      )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(StackOverflowNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
