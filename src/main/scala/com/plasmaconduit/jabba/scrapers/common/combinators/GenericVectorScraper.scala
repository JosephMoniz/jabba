package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}
import com.plasmaconduit.jabba.{ScraperStateMachine, URL}

object GenericVectorScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode], mapper: DomNode => Vector[String]) = {
    (machine: ScraperStateMachine, url: URL, document: DomRoot) =>
      Some(nodes(url, document).flatMap(mapper))
        .filter(_.length > 0)
        .map(n => Map[String, String](key -> n.mkString(",")))
  }

}
