package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQPresentationNode {

  val machine = ScraperStateMachine(
    "InfoQ_Presentation_Node",
    PendingScraper(),
    RunningScraper(
      sleep  = 30.seconds,
      scrape = scrape
    ),
    CompletedScraper()
  )

  def apply(): ScraperStateMachine = machine

  def scrape(machine: ScraperStateMachine, url: String, page: DomRoot): ScraperResult = {
    val data = scrapeDataFromArticle(page)
    ScraperResult(url, data, Vector(), machine)
  }

  def scrapeDataFromArticle(page: DomRoot): Option[Map[String, String]] = for(
    title            <- page.querySelectorAll("h1.general div").headOption.map(_.getText);
    authorTag        <- page.querySelector(".author_general a.editorlink");
    infoqAuthor      <- authorTag.getAttribute("href");
    displayAuthor    <- Some(authorTag.getText);
    imageTag         <- page.querySelector("meta[property='og:image']");
    image            <- imageTag.getAttribute("content");
    date             <- page.querySelector(".author_general").map(_.getText).map(cleanUpPublishDate);
    recordedAt       <- page.querySelector("h1.general .recorded a").flatMap(_.getAttribute("href")).orElse(Some(""));
    summary          <- page.querySelector("#summary").map(_.getText).map(cleanUpSummary);
    authorBio        <- page.querySelector("#biotext").map(_.getText);
    eventDescription <- page.querySelector("#conference").map(_.getText).orElse(Some(""))
  ) yield Map(
    "title"             -> title,
    "infoq_author"      -> infoqAuthor,
    "display_author"    -> displayAuthor,
    "image"             -> image,
    "date"              -> date,
    "recorded_at"       -> recordedAt,
    "summary"           -> summary,
    "author_bio"        -> authorBio,
    "event_description" -> eventDescription
  )

  def cleanUpPublishDate(string: String): String = string

  def cleanUpSummary(string: String): String = string

}
