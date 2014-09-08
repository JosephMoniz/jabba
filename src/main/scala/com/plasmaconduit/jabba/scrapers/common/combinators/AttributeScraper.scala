package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom.combinators.FirstNode
import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}

object AttributeScraper {

  def apply(key: String, attribute: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    AttributeVectorScraper(key, attribute, FirstNode(nodes))
  }

}
