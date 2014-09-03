package com.plasmaconduit.jabba.scrapers.infoq.presentations

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQPresentationNode {

  val dateRegex = """([a-z-A-Z]+ [0-9]{1,2}, [0-9]{4})""".r

  val summaryRegex = """Summary[^a-zA-Z0-9]*(.*)""".r

  val machine = ScraperStateMachine(
    name    = "InfoQ_Presentation_Node",
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
    "publish_date"      -> date.getOrElse(""),
    "recorded_at"       -> recordedAt,
    "summary"           -> summary.getOrElse(""),
    "author_bio"        -> authorBio,
    "event_description" -> eventDescription
  )

  def cleanUpPublishDate(string: String): Option[String] = {
    dateRegex findFirstIn string map { case dateRegex(date) => date }
  }

  def cleanUpSummary(string: String): Option[String] = {
    summaryRegex findFirstIn string map { case summaryRegex(summary) => summary }
  }

}
