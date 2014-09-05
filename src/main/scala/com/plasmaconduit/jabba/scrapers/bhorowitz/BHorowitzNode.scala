package com.plasmaconduit.jabba.scrapers.bhorowitz

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object BHorowitzNode {

  val machine = ScraperStateMachine(
    name = "BenHorowitz_Node",
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
    title       <- scrapeMetaProperty(document, "meta[property='og:title']");
    publishDate <- document.querySelector(".byline")
  ) yield Map(
    "title"          -> title,
    "publish_date"   -> publishDate.getText,
    "twitter_author" -> "https://twitter.com/bhorowitz",
    "publisher"      -> "http://www.bhorowitz.com/",
    "scraped_time"   -> new Date().getTime.toString
  )

  def scrapeMetaProperty(document: DomRoot, property: String): Option[String] = {
    document
      .querySelector(property)
      .flatMap(_.getAttribute("content"))
  }

}
