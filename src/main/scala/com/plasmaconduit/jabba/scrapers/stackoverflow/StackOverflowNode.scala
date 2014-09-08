package com.plasmaconduit.jabba.scrapers.stackoverflow

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.scrapers.common.combinators._
import scala.concurrent.duration._

object StackOverflowNode {

  val machine = ScraperStateMachine(
    name = "StackOverflow_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      15.seconds,
      scrape = NodeScraper(
        TextScraper("title", FirstNode(CssSelectorNodes("a.question-hyperlink"))),
        UrlLinksScraper("so_ask_user", FirstNode(CssSelectorNodes(".owner .user-details a"))),
        OptionalScraper(UrlLinksScraper("so_answer_users", DropNodes(1, CssSelectorNodes(".owner .user-details a")))),
        OptionalScraper(UrlLinksScraper("so_comment_users", CssSelectorNodes("a.comment-user"))),
        FixedScraper("publisher", "http://stackoverflow.com/"),
        TimedScraper()
      )
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
