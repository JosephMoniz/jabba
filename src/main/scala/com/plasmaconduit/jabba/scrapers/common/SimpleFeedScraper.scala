package com.plasmaconduit.jabba.scrapers.common

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._

object SimpleFeedScraper {

  def apply(nodeLinks: String,
            nodeTarget: => ScraperStateMachine,
            nextLink: String,
            nextTarget: => ScraperStateMachine): (ScraperStateMachine, URL, DomRoot) => ScraperResult =
    ClosureFeedScraper(
      nodeLinks  = (document: DomRoot) => document.querySelectorAll(nodeLinks),
      nodeTarget = nodeTarget,
      nextLinks  = (document: DomRoot) => document.querySelectorAll(nextLink).takeRight(1).toVector,
      nextTarget = nextTarget
    )

}
