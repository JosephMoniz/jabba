package com.plasmaconduit.jabba.scrapers.posthaven

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object PostHavenNode {

  val machine = ScraperStateMachine(
    name = "PostHaven_Node",
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
    val data = scrapeAuthorFromArticle(url, scrapeDataFromArticle(url, document), document)
    ScraperSuccess(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(url: URL, document: DomRoot): Option[Map[String, String]] = for (
    titleTag       <- document.querySelector("meta[property='og:title']");
    title          <- titleTag.getAttribute("content");
    descriptionTag <- document.querySelector("meta[property='og:description']");
    description    <- descriptionTag.getAttribute("content");
    dateTag        <- document.querySelector(".actual-date");
    date           <- dateTag.getAttribute("data-posthaven-date-utc-iso8601")
  ) yield Map(
    "title"       -> title,
    "description" -> description,
    "date"        -> date
  )

  def scrapeAuthorFromArticle(url: URL,
                              data: Option[Map[String, String]],
                              document: DomRoot): Option[Map[String, String]] =
  {
    val appended = for (
      map       <- data;
      authorTag <- document.querySelector(".author a");
      ph_author <- authorTag.getAttribute("href")
    ) yield map + ("ph_author" -> ph_author, "display_author" -> authorTag.getText)
    appended.orElse(data)
  }

}
