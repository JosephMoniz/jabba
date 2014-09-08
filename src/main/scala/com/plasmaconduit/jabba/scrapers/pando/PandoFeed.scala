package com.plasmaconduit.jabba.scrapers.pando

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba.scrapers.common._
import com.plasmaconduit.jabba.browsers.dom.combinators.{LastNode, CssSelectorNodes, DatedLinks}
import scala.concurrent.duration._

object PandoFeed {

  val linkPattern = """^http://pando.com/[0-9]{4}/[0-9]{2}/[0-9]{2}/.*/""".r

  val machine = ScraperStateMachine(
    name       = "Pando_Feed",
    pending    = PendingScraper(
      initialUrls = URLs("http://pando.com/")
    ),
    running    = RunningScraper(
      sleep  = 240.seconds,
      scrape = FeedScraper(
        nodeLinks  = DatedLinks(CssSelectorNodes("[data-pubdate] .text a, [data-pubdate] .entry-title a")),
        nodeTarget = PandoNode(),
        nextLinks  = LastNode(CssSelectorNodes(".pager h3 a")),
        nextTarget = PandoFeed()
      )
    ),
    completed  = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(PandoNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
