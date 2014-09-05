package com.plasmaconduit.jabba.scrapers.gigaom

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object GigaOmNode {

  val machine = ScraperStateMachine(
    name = "GigaOm_Node",
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
    title       <- scrapeMetaProperty(document, "og:title");
    description <- scrapeMetaProperty(document, "og:description");
    publishDate <- scrapeMetaProperty(document, "article:published_time")
  ) yield Map(
    "title"          -> title,
    "description"    -> description,
    "publish_date"   -> publishDate,
    "image"          -> scrapeImage(url, document),
    "gigaom_authors" -> scrapeMetaPropertyVector(document, "article:author").mkString(","),
    "tags"           -> scrapeMetaPropertyVector(document, "article:tag").mkString(","),
    "scraped_time"   -> new Date().getTime.toString
  )

  def scrapeMetaProperty(document: DomRoot, property: String): Option[String] = {
    document
      .querySelector(s"meta[property='$property']")
      .flatMap(_.getAttribute("content"))
  }

  def scrapeMetaPropertyVector(document: DomRoot, property: String): Vector[String] = {
    document
      .querySelectorAll(s"meta[property='$property']")
      .flatMap(_.getAttribute("content").toVector)
  }

  def scrapeImage(url: URL, document: DomRoot): String = {
    scrapeMetaProperty(document, "og:image")
      .flatMap(n => URL.canonicalize(url, n))
      .map(_.toString)
      .getOrElse("")
  }

}
