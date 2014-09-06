package com.plasmaconduit.jabba.scrapers.common.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom._

import scala.util.matching.Regex

object AttributePatternNodes {

  def apply(attribute: String, pattern: Regex, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    (url: URL, document: DomRoot) => nodes(url, document).filter(n =>
      n.getAttribute(attribute).filter(a => pattern.findFirstIn(a).isDefined).isDefined
    )
  }

}
