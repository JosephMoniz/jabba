package com.plasmaconduit.jabba.scrapers.a16z

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object A16zNode {

  val machine = ScraperStateMachine(
    name    = "A16z_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep = 15.seconds,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, document: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(url, document)
    ScraperSuccess(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(url: URL, document: DomRoot): Option[Map[String, String]] = for (
   title <- document.querySelector(".article h1")
  ) yield Map(
    "title"            -> title.getText,
    "publish_date"     -> url.toString.drop(16).take(10),
    "a16z_author"      -> document.querySelector(".post-byline").map(_.getText.drop(3)).getOrElse(""),
    "tags"             -> document.querySelectorAll("a[rel='tag']").map(_.getText).mkString(","),
    "scraped_time"     -> new Date().getTime.toString,
    "publisher"        -> "http://a16z.com/"
  )

}
