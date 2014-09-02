package com.plasmaconduit.jabba.browsers.dom

trait DomRoot {

  def querySelectorAll(selector: String): Vector[DomNode]

  def querySelector(s: String): Option[DomNode]

}
