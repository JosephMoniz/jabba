package com.plasmaconduit.jabba.scrapers.svbtle

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object SvbtleNode {

  val machine = ScraperStateMachine(
    name = "Svbtle_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 15.seconds,
      scrape = scrape
    ),
    completed = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, document: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(document)
    ScraperSuccess(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(document: DomRoot): Option[Map[String, String]] = for (
    title         <- document.querySelector(".article_title a").map(_.getText);
    timeTag       <- document.querySelector(".article_time");
    time          <- timeTag.getAttribute("datetime");
    twitterTag    <- document.querySelector("meta[property='twitter:creator']");
    twitterAuthor <- twitterTag.getAttribute("content").filter(_.length > 1).orElse(Some(""))
  ) yield Map(
      "title"          -> title,
      "date"           -> time,
      "twitter_author" -> twitterAuthor
    )

}
