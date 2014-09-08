package com.plasmaconduit.jabba.scrapers.bhorowitz

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba.browsers.dom.combinators.CssSelectorNodes
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators.{TimedScraper, FixedScraper, TextScraper, MetaContentScraper}
import scala.concurrent.duration._

object BHorowitzNode {

  val machine = ScraperStateMachine(
    name = "BenHorowitz_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        TextScraper("publish_date", CssSelectorNodes(".byline")),
        FixedScraper("twitter_author", "https://twitter.com/bhorowitz"),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
