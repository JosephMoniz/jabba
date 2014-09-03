package com.plasmaconduit.jabba.scrapers.paulgraham

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PaulGrahamNode {

  val dateRegex = "([a-zA-Z]+ [0-9]{4})".r

  val machine = ScraperStateMachine(
    name    = "PaulGraham_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      sleep  = 30.seconds,
      scrape = scrape
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )
  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: URL, page: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(page)
    ScraperSuccess(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(page: DomRoot): Option[Map[String, String]] = for (
    title <- page.querySelector("td[width='455'] img").flatMap(_.getAttribute("alt"));
    date  <- page.querySelector("td[width='455'] font[face='verdana']").map(_.getText).map(cleanUpDate)
  ) yield Map(
    "title"          -> title,
    "publish_date"   -> date.getOrElse(""),
    "twitter_author" -> "https://twitter.com/paulg"
  )

  def cleanUpDate(string: String): Option[String] = {
    dateRegex findFirstIn string map { case dateRegex(date) => date }
  }

}
