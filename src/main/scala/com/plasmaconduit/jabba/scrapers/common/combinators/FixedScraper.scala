package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.{ScraperStateMachine, URL}

object FixedScraper {

  def apply(key: String, value: String) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      Some(Map(key -> value))
  }

}
