package com.plasmaconduit.jabba

import com.plasmaconduit.jabba.browsers.drivers._
import com.plasmaconduit.jabba.ledgers._
import com.plasmaconduit.jabba.scrapers.a16z._
import com.plasmaconduit.jabba.scrapers.bhorowitz._
import com.plasmaconduit.jabba.scrapers.channel9._
import com.plasmaconduit.jabba.scrapers.gigaom._
import com.plasmaconduit.jabba.scrapers.infoq.interviews._
import com.plasmaconduit.jabba.scrapers.infoq.presentations._
import com.plasmaconduit.jabba.scrapers.pando._
import com.plasmaconduit.jabba.scrapers.paulgraham._
import com.plasmaconduit.jabba.scrapers.posthaven._
import com.plasmaconduit.jabba.scrapers.stackoverflow._
import com.plasmaconduit.jabba.scrapers.svbtle._
import com.plasmaconduit.jabba.scrapers.techcrunch._
import com.plasmaconduit.jabba.scrapers.theverge._

object Main {

  def main(args: Array[String]) {
    val factory    = BrowserFactory(HtmlUnitBrowser())
    val transactor = Transactor(factory)
    val ledger     = MemoryLedger()
    val scrapers   = Scrapers.fromStateMachineVector(Vector(
    /*
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
      StackOverflowNode(),
      GigaOmFeed(),
      GigaOmNode(),
      Channel9Feed(),
      Channel9Node(),
      PandoFeed(),
      PandoNode(),
      TheVergeFeed(),
      TheVergeNode(),
      */
      A16zFeed(),
      A16zNode()
      //BHorowitzFeed(),
      //BHorowitzNode()
    ))
    val scraper = Jabba(transactor, ledger, scrapers)
    scraper.scrape()
  }

}
