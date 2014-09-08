package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.{URLs, URL, ScraperStateMachine}

object CanonicalizeUrlsScraper {

  def apply(scraper: (ScraperStateMachine, URL, DomRoot) => Option[Map[String, String]]) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      scraper(machine, url, document).map(n =>
        n.mapValues(s =>
          URLs(s.split(",").toVector).mkString(",")
        )
      )
  }

}
