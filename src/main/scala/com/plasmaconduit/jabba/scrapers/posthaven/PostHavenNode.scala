package com.plasmaconduit.jabba.scrapers.posthaven

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object PostHavenNode {

  val machine = ScraperStateMachine(
    name = "PostHaven_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        MetaContentScraper("description", CssSelectorNodes("meta[property='og:description']")),
        AttributeScraper("date", "data-posthaven-date-utc-iso8601", CssSelectorNodes(".actual-date")),
        OptionalScraper(AttributeScraper("ph_author", "href", CssSelectorNodes(".author a"))),
        OptionalScraper(TextScraper("display_author", CssSelectorNodes(".author a"))),
        OptionalScraper((m, u, d) => Some(Map("publisher" -> u.toBase))),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
