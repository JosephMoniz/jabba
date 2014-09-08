package com.plasmaconduit.jabba.browsers.dom.combinators

object RelNextLink {

  def apply() = {
    FirstNode(CssSelectorNodes("a[rel='next']"))
  }

}
