package com.plasmaconduit.jabba.scrapers.common.combinators

import java.util.Date

import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.{ScraperStateMachine, URL}

object TimedScraper {

  def apply() = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      Some(Map("scraped_time" -> new Date().getTime.toString))
  }

}
