package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom.{DomRoot, DomNode}

object MetaContentScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    AttributeScraper(key, "content", nodes)
  }

}
