package com.plasmaconduit.jabba.scrapers.channel9

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.DomRoot
import scala.concurrent.duration._

object Channel9Node {

  val machine = ScraperStateMachine(
    name       = "Channel9_Node",
    pending    = PendingScraper(),
    running    = RunningScraper(
      sleep  = 15.seconds,
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
    title       <- scrapeMetaProperty(document, "meta[property='og:title']");
    description <- scrapeMetaProperty(document, "meta[property='og:description']");
    video       <- scrapeMetaProperty(document, "meta[property='og:video']");
    secureVideo <- scrapeMetaProperty(document, "meta[property='og:video:secure_url']")
  ) yield Map(
      "title"            -> title,
      "description"      -> description,
      "image"            -> scrapeImage(url, document),
      "mp4_video"        -> video,
      "secure_mp4_video" -> secureVideo,
      "tags"             -> scrapeMetaProperty(document, "meta[name='keywords']").getOrElse(""),
      "publisher"        -> "http://channel9.msdn.com/",
      "scraped_time"     -> new Date().getTime.toString
    )

  def scrapeMetaProperty(document: DomRoot, property: String): Option[String] = {
    document
      .querySelector(property)
      .flatMap(_.getAttribute("content"))
  }

  def scrapeImage(url: URL, document: DomRoot): String = {
    scrapeMetaProperty(document, "meta[property='og:image']")
      .flatMap(n => URL.canonicalize(url, n))
      .map(_.toString)
      .getOrElse("")
  }

}
