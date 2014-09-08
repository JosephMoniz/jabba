package com.plasmaconduit.jabba.scrapers.theverge

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object TheVergeNode {

  val machine = ScraperStateMachine(
    name = "TheVerge_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        MetaContentScraper("description", CssSelectorNodes("meta[property='og:description']")),
        MetaContentScraper("publish_date", CssSelectorNodes("meta[name='sailthru.date']")),
        OptionalScraper(CanonicalizeUrlsScraper(MetaContentScraper("image", CssSelectorNodes("meta[property='og:image']")))),
        AttributeScraper("theverge_author", "href", CssSelectorNodes(".author a")),
        TextScraper("display_author", CssSelectorNodes(".author a")),
        FixedScraper("publisher", "http://www.theverge.com/"),
        MetaContentScraper("tags", CssSelectorNodes("meta[name='sailthru.tags']")),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
