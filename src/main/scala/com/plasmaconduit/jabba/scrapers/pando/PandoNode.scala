package com.plasmaconduit.jabba.scrapers.pando

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators.CssSelectorNodes
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object PandoNode {

  val machine = ScraperStateMachine(
    name = "Pando_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        MetaContentScraper("description", CssSelectorNodes("meta[property='og:description']")),
        MetaContentScraper("publish_date", CssSelectorNodes("meta[property='article:published_time']")),
        OptionalScraper(MetaContentScraper("image", CssSelectorNodes("meta[property='og:image']"))),
        MetaContentVectorScraper("pando_authors", CssSelectorNodes("meta[property='article:author']")),
        FixedScraper("publisher", "http://pando.com/"),
        TimedScraper()
      )
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
