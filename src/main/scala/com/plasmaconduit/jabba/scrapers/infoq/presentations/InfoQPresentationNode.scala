package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object InfoQPresentationNode {

  val dateRegex = """([a-z-A-Z]+ [0-9]{1,2}, [0-9]{4})""".r

  val summaryRegex = """Summary[^a-zA-Z0-9]*(.*)""".r

  val machine = ScraperStateMachine(
    name    = "InfoQ_Presentation_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        TextScraper("title", FirstNode(CssSelectorNodes("h1.general div"))),
        AttributeScraper("infoq_author", "href", CssSelectorNodes(".author_general a.editorlink")),
        TextScraper("display_author", CssSelectorNodes(".author_general a.editorlink")),
        MetaContentScraper("image", CssSelectorNodes("meta[property='og:image']")),
        RegexFilterScraper(dateRegex, TextScraper("date", CssSelectorNodes(".author_general"))),
        OptionalScraper(AttributeScraper("recorded_at", "href", CssSelectorNodes("h1.general .recorded a"))),
        RegexFilterScraper(summaryRegex, TextScraper("summary", CssSelectorNodes("#summary"))),
        TextScraper("author_bio", CssSelectorNodes("#biotext")),
        OptionalScraper(TextScraper("event_description", CssSelectorNodes("#conference"))),
        FixedScraper("publisher", "http://infoq.com/"),
        TimedScraper()
      )
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
