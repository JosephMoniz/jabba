package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object TechCrunchNode {

  val machine = ScraperStateMachine(
    name = "TechCrunch_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        TextScraper("title", CssSelectorNodes("h1.alpha.tweet-title")),
        OptionalScraper(MetaContentScraper("image", CssSelectorNodes("meta[name='twitter:image:src']"))),
        MetaContentScraper("tags", CssSelectorNodes("meta[name='sailthru.tags']")),
        MetaContentScraper("time", CssSelectorNodes("meta[name='sailthru.date']")),
        AttributeScraper("tc_author", "href", CssSelectorNodes(".title-left .byline a[rel='author']")),
        AttributeScraper("twitter_author", "href", CssSelectorNodes(".title-left .byline a[rel='external']")),
        MetaContentScraper("description", CssSelectorNodes("meta[name='twitter:description']")),
        FixedScraper("publisher", "http://techcrunch.com/"),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
