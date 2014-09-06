package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom._

object DatedLinks {

  def apply(nodes: (URL, DomRoot) => Vector[DomNode]) = {
    (url: URL, document: DomRoot) => {
      val pattern = s"""^${url.toBase}/[0-9]{4}/[0-9]{2}/[0-9]{2}/.*/""".r
      AttributePatternNodes("href", pattern, nodes)(url, document)
    }
  }

}
