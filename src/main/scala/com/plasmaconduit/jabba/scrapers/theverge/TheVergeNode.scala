package com.plasmaconduit.jabba.scrapers.theverge

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object TheVergeNode {

  val machine = ScraperStateMachine(
    name = "TheVerge_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = scrape
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, document: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(url, document)
    ScraperSuccess(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(url: URL, document: DomRoot): Option[Map[String, String]] = for (
    title          <- scrapeMetaProperty(document, "meta[property='og:title']");
    description    <- scrapeMetaProperty(document, "meta[property='og:description']");
    publishDate    <- scrapeMetaProperty(document, "meta[name='sailthru.date']");
    authorTag      <- document.querySelector(".author a");
    thevergeAuthor <- authorTag.getAttribute("href");
    tags           <- scrapeMetaProperty(document, "meta[name='sailthru.tags']")
  ) yield Map(
    "title"            -> title,
    "description"      -> description,
    "publish_date"     -> publishDate,
    "image"            -> scrapeImage(url, document),
    "theverge_author"  -> thevergeAuthor,
    "display_author"   -> authorTag.getText,
    "tags"             -> tags,
    "publisher"        -> "http://www.theverge.com/"
  )

  def scrapeMetaProperty(document: DomRoot, property: String): Option[String] = {
    document
      .querySelector(property)
      .flatMap(_.getAttribute("content"))
  }

  def scrapeMetaPropertyVector(document: DomRoot, property: String): Vector[String] = {
    document
      .querySelectorAll(property)
      .flatMap(_.getAttribute("content").toVector)
  }

  def scrapeImage(url: URL, document: DomRoot): String = {
    scrapeMetaProperty(document, "meta[property='og:image']")
      .flatMap(n => URL.canonicalize(url, n))
      .map(_.toString)
      .getOrElse("")
  }

}
