package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}

object AttributeVectorScraper {

  def apply(key: String, attribute: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    GenericVectorScraper(key, nodes, n => n.getAttribute(attribute).toVector)
  }

}
