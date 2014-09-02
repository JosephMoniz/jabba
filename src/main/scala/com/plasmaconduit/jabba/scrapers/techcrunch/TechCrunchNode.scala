package com.plasmaconduit.jabba.scrapers.techcrunch

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import scala.concurrent.duration._

object TechCrunchNode {

  val machine = ScraperStateMachine(
    "TechCrunch_Node",
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

  def scrapeDataFromArticle(page: DomRoot): Option[Map[String, String]] = for (
    title          <- page.querySelector("h1.alpha.tweet-title").map(_.getText);
    image          <- Some(scrapeImageFromArticle(page));
    tagsTag        <- page.querySelector("meta[name='sailthru.tags']");
    tags           <- tagsTag.getAttribute("content");
    timeTag        <- page.querySelector("meta[name='sailthru.date']");
    time           <- timeTag.getAttribute("content");
    authorTag      <- page.querySelector(".title-left .byline a[rel='author']");
    displayAuthor  <- Some(authorTag.getText);
    tcAuthor       <- authorTag.getAttribute("href");
    twitterTag     <- page.querySelector(".title-left .byline a[rel='external']");
    twitterAuthor  <- twitterTag.getAttribute("href");
    descriptionTag <- page.querySelector("meta[name='twitter:description']");
    description    <- descriptionTag.getAttribute("content");
    article        <- page.querySelector(".article-entry").map(_.getText)
  ) yield Map(
    "title"           -> title,
    "image"           -> image,
    "tags"            -> tags,
    "time"            -> time,
    "display_author"  -> displayAuthor,
    "tc_author"       -> tcAuthor,
    "twitter_author"  -> twitterAuthor,
    "description"     -> description
  )

  def scrapeImageFromArticle(page: DomRoot): String =
    page
      .querySelector("meta[name='twitter:image:src']")
      .flatMap(_.getAttribute("content"))
      .orElse({
        page
          .querySelector("meta[property='og:image']")
          .flatMap(_.getAttribute("content"))
      })
      .getOrElse("")

}
