package com.plasmaconduit.jabba.browsers.dom.combinators

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._

object CssSelectorNodes {

  def apply(selector: String) = {
    (url: URL, document: DomRoot) => document.querySelectorAll(selector)
  }

}
