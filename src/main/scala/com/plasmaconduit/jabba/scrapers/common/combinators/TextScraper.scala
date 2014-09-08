package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom.combinators.FirstNode
import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}

object TextScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    TextVectorScraper(key, FirstNode(nodes))
  }

}
