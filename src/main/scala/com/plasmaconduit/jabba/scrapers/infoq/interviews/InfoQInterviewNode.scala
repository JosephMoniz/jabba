package com.plasmaconduit.jabba.scrapers.infoq.interviews

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object InfoQInterviewNode {

  val machine = ScraperStateMachine(
    "InfoQ_Interview_Node",
    PendingScraper(),
    RunningScraper(
      sleep  = 15.seconds,
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
     title              <- page.querySelectorAll("h1.general div").headOption.map(_.getText);
     authorTag          <- page.querySelectorAll(".author_general a").headOption;
     infoqAuthor        <- authorTag.getAttribute("href");
     displayAuthor      <- Some(authorTag.getText);
     interviewerTag     <- page.querySelectorAll(".author_general a").tail.headOption;
     infoqInterviewer   <- interviewerTag.getAttribute("href");
     displayInterviewer <- Some(interviewerTag.getText);
     imageTag           <- page.querySelector("meta[property='og:image']");
     image              <- imageTag.getAttribute("content");
     date               <- page.querySelector(".author_general").map(_.getText).map(cleanUpPublishDate); // different
     recordedAt         <- page.querySelector("h1 .recorded a").flatMap(_.getAttribute("href")).orElse(Some(""));
     authorBio          <- page.querySelectorAll("#leftside p").headOption.map(_.getText);
     eventDescription   <- page.querySelectorAll("#leftside p").lastOption.map(_.getText)
   ) yield Map(
     "title"               -> title,
     "infoq_author"        -> infoqAuthor,
     "display_author"      -> displayAuthor,
     "infoq_interviewer"   -> infoqInterviewer,
     "display_interviewer" -> displayInterviewer,
     "image"               -> image,
     "date"                -> date,
     "recorded_at"         -> recordedAt,
     "author_bio"          -> authorBio,
     "event_description"   -> eventDescription
   )

  def cleanUpPublishDate(string: String): String = string

  def cleanUpSummary(string: String): String = string

  def cleanUpBio(string: String): String = string

  def cleanUpEventDescription(string: String): String = string

 }
