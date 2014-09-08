package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.{ScraperStateMachine, URL}

object OptionalScraper {

  def apply(scraper: (ScraperStateMachine, URL, DomRoot) => Option[Map[String, String]]) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      scraper(machine, url, document).orElse(Some[Map[String, String]](Map[String, String]()))
  }

}
