package com.plasmaconduit.jabba.scrapers.a16z

import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom._
import com.plasmaconduit.jabba.scrapers.common._
import scala.concurrent.duration._

object A16zFeed {

  val linkPattern = """^http://a16z.com/[0-9]{4}/[0-9]{2}/[0-9]{2}/.*/""".r

  val machine = ScraperStateMachine(
    name = "A16z_Feed",
    pending = PendingScraper(
      initialUrls = URLs("http://a16z.com/")
    ),
    running = RunningScraper(
      sleep = 150.seconds,
      scrape = ClosureFeedScraper(
        nodeLinks  = (document: DomRoot) =>
          document
            .querySelectorAll(".post a")
            .filter(n => n.getAttribute("href").flatMap(m => linkPattern.findFirstIn(m)).isDefined),
        nodeTarget = A16zNode(),
        nextLinks  = (document: DomRoot) => document.querySelectorAll(".nav-previous a"),
        nextTarget = A16zFeed()
      )
    ),
    completed = CompletedScraper(),
    assertions = ScraperAssertions(
      MustContainTargets,
      MustContainTargetsFor(A16zNode())
    )
  )

  def apply(): ScraperStateMachine = machine

}
