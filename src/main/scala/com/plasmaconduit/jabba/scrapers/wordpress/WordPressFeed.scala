/*
package com.plasmaconduit.jabba.scrapers.wordpress

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import scala.concurrent.duration._

object WordPressFeed {

  val machine = ScraperStateMachine(
    name = "WordPress_Feed",
    pending = PendingScraper(
      initialUrls = URLs(
        "http://john.a16z.com/",  // http://john.a16z.com/page/2/
        "http://jeff.a16z.com/",  // http://jeff.a16z.com/page/2/
        "http://peter.a16z.com/", // http://peter.a16z.com/page/2/
        "http://scott.a16z.com/"  // http://scott.a16z.com/page/2/
      )
    ),
    running = RunningScraper(
      sleep  = 150.seconds,
      scrape = FeedScraper(
        nodeLinks  = DatedLinks(CssSelectorNodes(".post-meta")),
        nodeTarget = WordPressNode(),
        nextLinks  = CssSelectorNodes(".nav-previous a"),
        nextTarget = WordPressFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions =  ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(WordPressNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
*/