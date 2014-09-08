package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}

object TextVectorScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    GenericVectorScraper(key, nodes, n => Vector(n.getText))
  }

}
