package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object InfoQInterviewNode {

  val dateR = """([a-z-A-Z]+ [0-9]{1,2}, [0-9]{4})""".r

  val bioR = """Bio[^a-zA-Z0-9]*(.*)""".r

  val machine = ScraperStateMachine(
    name    = "InfoQ_Interview_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        TextScraper("title", CssSelectorNodes("h1.general div")),
        AttributeScraper("infoq_author", "href", FirstNode(CssSelectorNodes(".author_general a"))),
        TextScraper("display_author", CssSelectorNodes(".author_general a")),
        AttributeScraper("infoq_interviewer", "href", LastNode(CssSelectorNodes(".author_general a"))),
        TextScraper("display_interviewer", CssSelectorNodes(".author_general a")),
        MetaContentScraper("image", CssSelectorNodes("meta[property='og:image']")),
        OptionalScraper(RegexFilterScraper(dateR, TextScraper("publish_date", CssSelectorNodes(".author_general")))),
        OptionalScraper(AttributeScraper("recorded_at", "href", CssSelectorNodes("h1 .recorded a"))),
        RegexFilterScraper(bioR, TextScraper("author_bio", FirstNode(CssSelectorNodes("#leftside p")))),
        TextScraper("event_description", LastNode(CssSelectorNodes("#leftside p"))),
        FixedScraper("publisher", "http://infoq.com/"),
        TimedScraper()
      )
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )

   def apply(): ScraperStateMachine = machine

 }
