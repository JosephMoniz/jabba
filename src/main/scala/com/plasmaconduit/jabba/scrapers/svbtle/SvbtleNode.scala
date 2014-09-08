package com.plasmaconduit.jabba.scrapers.svbtle

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object SvbtleNode {

  val machine = ScraperStateMachine(
    name = "Svbtle_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        TextScraper("title", CssSelectorNodes(".article_title a")),
        AttributeScraper("date", "datetime", CssSelectorNodes(".article_time")),
        OptionalScraper(MetaContentScraper("twitter_author", CssSelectorNodes("meta[property='twitter:creator']"))),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
