package com.plasmaconduit.jabba.scrapers.gigaom

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba.browsers.dom.combinators.CssSelectorNodes
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object GigaOmNode {

  val machine = ScraperStateMachine(
    name = "GigaOm_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        MetaContentScraper("description", CssSelectorNodes("meta[property='og:description']")),
        MetaContentScraper("publish_date", CssSelectorNodes("meta[property='article:published_time']")),
        OptionalScraper(MetaContentScraper("image", CssSelectorNodes("meta[property='og:image']"))),
        MetaContentVectorScraper("gigaom_authors", CssSelectorNodes("meta[property='article:author']")),
        MetaContentVectorScraper("tags", CssSelectorNodes("meta[property='article:tag']")),
        FixedScraper("publisher", "https://gigaom.com/"),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
