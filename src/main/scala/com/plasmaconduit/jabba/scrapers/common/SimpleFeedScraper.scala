package com.plasmaconduit.jabba.scrapers.common

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._

object SimpleFeedScraper {

  def apply(nodeLinks: String,
            nodeTarget: => ScraperStateMachine,
            nextLink: String,
            nextTarget: => ScraperStateMachine): (ScraperStateMachine, URL, DomRoot) => ScraperResult =
  {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      val targets      = scrapeNodeLinks(nodeLinks, nodeTarget, url, document)
      val nextPage     = scrapeNextLink(nextLink, nextTarget, url, document)
      val allTargets   = nextPage.map(n => targets :+ n).getOrElse(targets)
      ScraperSuccess(url, None, allTargets, machine)
  }

  def scrapeNodeLinks(selector: String,
                      target: ScraperStateMachine,
                      base: URL,
                      document: DomRoot): Vector[ScraperTarget] =
  for (
    link      <- document.querySelectorAll(selector);
    url       <- link.getAttribute("href").toVector;
    canonical <- URL.canonicalize(base, url).toVector
  ) yield ScraperTarget(target, canonical)

  def scrapeNextLink(selector: String,
                     target: ScraperStateMachine,
                     base: URL,
                     document: DomRoot): Option[ScraperTarget] =
  for (
    link      <- document.querySelectorAll(selector).lastOption;
    url       <- link.getAttribute("href");
    canonical <- URL.canonicalize(base, url)
  ) yield ScraperTarget(target, canonical)

}
