package com.plasmaconduit.jabba.browsers.dom.webdriver

import scala.collection.JavaConversions._
import com.plasmaconduit.jabba.browsers.dom.DomNode
import org.openqa.selenium._

final case class WebDriverDomNode(element: WebElement) extends DomNode {

  override def getAttribute(attribute: String): Option[String] =
    Option(element.getAttribute(attribute))

  def getText: String = element.getText

  def querySelectorAll(s: String): Vector[DomNode] =
    try {
      element.findElements(By.cssSelector(s)).toVector.map(WebDriverDomNode)
    } catch {
      case e: Exception => Vector()
    }

  def querySelector(s: String): Option[DomNode] =
    try {
      Option(element.findElement(By.cssSelector(s))).map(WebDriverDomNode)
    } catch {
      case e: Exception => None
    }

}
