package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom._

object UrlLinksScraper {

  def apply(key: String, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    CanonicalizeUrlsScraper(AttributeScraper(key, "href", nodes))
  }

}
