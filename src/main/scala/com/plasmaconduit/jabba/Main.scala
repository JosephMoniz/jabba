package com.plasmaconduit.jabba

import com.plasmaconduit.jabba.browsers.drivers._
import com.plasmaconduit.jabba.ledgers._
import com.plasmaconduit.jabba.scrapers.infoq.interviews._
import com.plasmaconduit.jabba.scrapers.infoq.presentations._
import com.plasmaconduit.jabba.scrapers.paulgraham._
import com.plasmaconduit.jabba.scrapers.posthaven._
import com.plasmaconduit.jabba.scrapers.stackoverflow._
import com.plasmaconduit.jabba.scrapers.svbtle._
import com.plasmaconduit.jabba.scrapers.techcrunch._

object Main {

  def main(args: Array[String]) {
    val factory    = BrowserFactory(HtmlUnitBrowser())
    val transactor = Transactor(factory)
    val ledger     = MemoryLedger()
    val scrapers   = Scrapers.fromStateMachineVector(Vector(
      TechCrunchFeed(),
      TechCrunchNode(),
      InfoQPresentationFeed(),
      InfoQPresentationNode(),
      InfoQInterviewFeed(),
      InfoQInterviewNode(),
      PaulGrahamFeed(),
      PaulGrahamNode(),
      PostHavenFeed(),
      PostHavenNode(),
      SvbtleFeed(),
      SvbtleNode(),
      StackOverflowFeed(),
      StackOverflowNode()
    ))
    val scraper = Jabba(transactor, ledger, scrapers)
    scraper.scrape()
  }

}
