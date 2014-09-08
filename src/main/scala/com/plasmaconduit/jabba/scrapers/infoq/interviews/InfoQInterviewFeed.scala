package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import scala.concurrent.duration._

object InfoQInterviewFeed {

  val machine = ScraperStateMachine(
    name    = "InfoQ_Interview_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.infoq.com/interviews/")
    ),
    running = RunningScraper(
      sleep  = 180.seconds,
      scrape = FeedScraper(
        nodeLinks  = CssSelectorNodes(".news_type_video h2 a"),
        nodeTarget = InfoQInterviewNode(),
        nextLinks  = LastNode(CssSelectorNodes(".load_more_articles a.blue")),
        nextTarget = InfoQInterviewFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(InfoQInterviewNode())
    )
  )

   def apply(): ScraperStateMachine = machine

 }
