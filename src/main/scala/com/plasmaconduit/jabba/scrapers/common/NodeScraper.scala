package com.plasmaconduit.jabba.scrapers.common

import java.util.Date
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.combinators._
import com.plasmaconduit.jabba.scrapers.common.combinators.GenericVectorScraper

object NodeScraper {

  def apply(scrapers: ((ScraperStateMachine, URL, DomRoot) => Option[Map[String, String]])*) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      ScraperSuccess(
        url  = url,
        data = scrapers.foldLeft[Option[Map[String, String]]](Some(Map[String, String]())) {(m, n) =>
          for (
            previous <- m;
            current  <- n(machine, url, document)
          ) yield previous ++ current
        },
        targets = Vector(),
        scraper = machine
      )
  }

}















