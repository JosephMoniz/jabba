package com.plasmaconduit.jabba.browsers.dom.webdriver

import scala.collection.JavaConversions._
import com.plasmaconduit.jabba.browsers.dom.{DomNode, DomRoot}
import org.openqa.selenium._

final case class WebDriverDomRoot(driver: WebDriver) extends DomRoot {

  def querySelectorAll(s: String): Vector[DomNode] =
    try {
      driver.findElements(By.cssSelector(s)).toVector.map(WebDriverDomNode)
    } catch {
      case e: Exception => Vector()
    }

  def querySelector(s: String): Option[DomNode] =
    try {
      Option(driver.findElement(By.cssSelector(s))).map(WebDriverDomNode)
    } catch {
      case e: Exception => None
    }

}
