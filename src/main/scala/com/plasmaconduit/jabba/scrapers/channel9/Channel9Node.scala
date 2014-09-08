package com.plasmaconduit.jabba.scrapers.channel9

import java.util.Date
import com.plasmaconduit.jabba._
import com.plasmaconduit.jabba.browsers.dom.DomRoot
import com.plasmaconduit.jabba.browsers.dom.combinators.CssSelectorNodes
import com.plasmaconduit.jabba.scrapers.common.NodeScraper
import com.plasmaconduit.jabba.scrapers.common.combinators.{TimedScraper, FixedScraper, MetaContentVectorScraper, MetaContentScraper}
import scala.concurrent.duration._

object Channel9Node {

  val machine = ScraperStateMachine(
    name       = "Channel9_Node",
    pending    = PendingScraper(),
    running    = RunningScraper(
      sleep  = 15.seconds,
      scrape = NodeScraper(
        MetaContentScraper("title", CssSelectorNodes("meta[property='og:title']")),
        MetaContentScraper("description", CssSelectorNodes("meta[property='og:description']")),
        MetaContentScraper("video", CssSelectorNodes("meta[property='og:video']")),
        MetaContentScraper("secure_mp4_video", CssSelectorNodes("meta[property='og:video:secure_url']")),
        MetaContentVectorScraper("tags", CssSelectorNodes("meta[name='keywords']")),
        FixedScraper("publisher", "http://channel9.msdn.com/"),
        TimedScraper()
      )
    ),
    completed  = CompletedScraper(),
    assertions = MustContainData
  )

  def apply(): ScraperStateMachine = machine

}
