package com.plasmaconduit.jabba

import com.plasmaconduit.jabba.browsers.dom._
import rx.lang.scala._

sealed trait Browser

case class BrowserFactory(driver: BrowserDriver) {

  def open: Observable[OpenBrowser] = driver.open

}

case class OpenBrowser(driver: BrowserDriver) {

  def visit(url: URL): Observable[BrowsingBrowser] = driver.visit(url)

}

case class BrowsingBrowser(driver: BrowserDriver) {

  def visit(url: URL): Observable[BrowsingBrowser] = driver.visit(url)

  def close: Observable[Unit] = driver.close

  def document: DomRoot = driver.document

}

trait BrowserDriver {

  def open: Observable[OpenBrowser]

  def visit(url: URL): Observable[BrowsingBrowser]

  def document: DomRoot

  def close: Observable[Unit]

}
