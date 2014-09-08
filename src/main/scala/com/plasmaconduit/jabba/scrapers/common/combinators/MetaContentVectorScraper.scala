package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._

object MetaContentVectorScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    AttributeScraper(key, "content", nodes)
  }

}
