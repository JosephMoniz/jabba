package com.plasmaconduit.jabba.scrapers.common.combinators

object RelNextLink {

  def apply() = {
    FirstNode(CssSelectorNodes("a[rel='next']"))
  }

}
