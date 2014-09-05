package com.plasmaconduit.jabba.scrapers.stackoverflow

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object StackOverflowNode {

  val machine = ScraperStateMachine(
    name = "StackOverflow_Node",
    pending = PendingScraper(),
    running = RunningScraper(
      15.seconds,
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

  def scrapeDataFromArticle(url: URL, document: DomRoot): Option[Map[String, String]] = {
    document.querySelector("a.question-hyperlink").map({ titleTag => Map(
      "title"            -> titleTag.getText,
      "so_ask_user"      -> verifyLinks(url, document.querySelectorAll(".owner .user-details a").take(1)),
      "so_answer_users"  -> verifyLinks(url, document.querySelectorAll(".user-details a").drop(1)),
      "so_comment_users" -> verifyLinks(url, document.querySelectorAll("a.comment-user")),
      "scraped_time"     -> new Date().getTime.toString
    )})
  }

  def canonicalizeLinks(url: URL, tags: Vector[DomNode]): Vector[String] = for (
    node          <- tags;
    link          <- node.getAttribute("href");
    canonicalized <- URL.canonicalize(url, link)
  ) yield canonicalized.toString

  def verifyLinks(url: URL, tags: Vector[DomNode]): String = {
    canonicalizeLinks(url, tags).mkString(",")
  }

}
