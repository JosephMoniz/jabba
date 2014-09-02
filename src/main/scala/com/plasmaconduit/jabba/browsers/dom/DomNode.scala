package com.plasmaconduit.jabba.browsers.dom

trait DomNode extends DomRoot {

  def getAttribute(attribute: String): Option[String]

  def getText: String

}
