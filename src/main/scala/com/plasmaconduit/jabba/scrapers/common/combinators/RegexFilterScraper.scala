package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.{URL, ScraperStateMachine}

import scala.util.matching.Regex

object RegexFilterScraper {

  def apply(regex: Regex, scraper: (ScraperStateMachine, URL, DomRoot) => Option[Map[String, String]]) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      scraper(machine, url, document).map(map =>
        map.mapValues(v =>
          v.split(",").flatMap(s =>
            regex.findFirstMatchIn(s).map(m =>
              m.subgroups.headOption
            )
          ).mkString(",")
        )
      )
  }

}
