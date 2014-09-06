package com.plasmaconduit.jabba.scrapers.common

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._

object FeedScraper {

  def apply(nodeLinks: (URL, DomRoot) => Vector[DomNode],
            nodeTarget: => ScraperStateMachine,
            nextLinks: (URL, DomRoot) => Vector[DomNode],
            nextTarget: => ScraperStateMachine): (ScraperStateMachine, URL, DomRoot) => ScraperResult =
  {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      val targets    = scrapeURLs(nodeLinks(url, document), nodeTarget, url, document)
      val nextPages  = scrapeURLs(nextLinks(url, document), nextTarget, url, document)
      val allTargets = targets ++ nextPages
      ScraperSuccess(url, None, allTargets, machine)
  }

  def scrapeURLs(links: Vector[DomNode],
                      target: ScraperStateMachine,
                      base: URL,
                      document: DomRoot): Vector[ScraperTarget] =
    for (
      link      <- links;
      url       <- link.getAttribute("href").toVector;
      canonical <- URL.canonicalize(base, url).toVector
    ) yield ScraperTarget(target, canonical)

}
