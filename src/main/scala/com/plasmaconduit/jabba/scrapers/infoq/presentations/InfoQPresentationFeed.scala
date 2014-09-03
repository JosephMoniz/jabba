package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object InfoQPresentationFeed {

  val machine = ScraperStateMachine(
    name    = "InfoQ_Presentation_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://www.infoq.com/presentations/")
    ),
    running = RunningScraper(
      sleep  = 180.seconds,
      scrape = SimpleFeedScraper(
        nodeLinks  = ".news_type_video h2 a",
        nodeTarget = InfoQPresentationNode(),
        nextLink   = ".load_more_articles a.blue",
        nextTarget = InfoQPresentationFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(InfoQPresentationNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
