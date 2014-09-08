package com.plasmaconduit.jabba.browsers.drivers

import java.util.logging._
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba.browsers.dom.webdriver._
import org.openqa.selenium._
import org.openqa.selenium.htmlunit._
import rx.lang.scala._

final case class HtmlUnitBrowser(driver: Option[WebDriver] = None) extends BrowserDriver {

  def open: Observable[OpenBrowser] = {
    Logger.getLogger("").setLevel(Level.OFF)
    Observable.just(OpenBrowser(HtmlUnitBrowser(Some(new HtmlUnitDriver()))))
  }

  def visit(url: URL): Observable[BrowsingBrowser] = driver match {
    case None    => Observable.error(new Exception("Invalid browser state"))
    case Some(n) => Observable.create({(observer) =>
      n.get(url.toString)
      observer.onNext(BrowsingBrowser(this))
      observer.onCompleted()
      Subscription()
    })
  }

  def document: DomRoot = driver match {
    case None    => throw new Exception("Invalid browser state")
    case Some(n) => WebDriverDomRoot(n)
  }

  def close: Observable[Unit] = Observable.create({(observer) =>
    driver match {
      case Some(n) => n.close()
      case _       => Unit
    }
    observer.onNext(Unit)
    observer.onCompleted()
    Subscription()
  })

}
