package com.plasmaconduit.jabba.browsers.dom.combinators

import com.plasmaconduit.jabba.URL
import com.plasmaconduit.jabba.browsers.dom._

object DropNodes {

  def apply(count: Int, nodes: (URL, DomRoot) => Vector[DomNode]) = {
    (url: URL, document: DomRoot) => nodes(url, document).drop(count)
  }

}
