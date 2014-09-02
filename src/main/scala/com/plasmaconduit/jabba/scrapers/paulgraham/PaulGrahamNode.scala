package com.plasmaconduit.jabba.scrapers.paulgraham

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PaulGrahamNode {

  val machine = ScraperStateMachine(
    "PaulGraham_Node",
    PendingScraper(),
    RunningScraper(
      sleep  = 30.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )
  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: Url, page: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(page)
    ScraperResult(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(page: DomRoot): Option[Map[String, String]] = for (
    title <- page.querySelector("td[width='455'] img").flatMap(_.getAttribute("alt"));
    date  <- page.querySelector("td[width='455'] font[face='verdana']").map(_.getText).map(cleanUpDate)
  ) yield Map(
    "title"          -> title,
    "date"           -> date,
    "twitter_author" -> "https://twitter.com/paulg"
  )

  def cleanUpDate(string: String): String = string

}
