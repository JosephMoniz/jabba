package com.plasmaconduit.jabba

import rx.lang.scala._

trait Ledger {
  def record(entry: LedgerEntry): Observable[Ledger]
  def readFromBeginning: Observable[LedgerRead]
  def readFromRecent: Observable[LedgerRead]
}

case class LedgerRead(ledger: Ledger, records: Observable[LedgerEntry])

case class ScraperTargetAttribute(scraper: String, url: String)

case class ScraperStateChangeAttribute(scraper: String,
                                       state: ScraperActivityState)

sealed trait LedgerEntry

case class ScraperResultEntry(
  timestamp: Long,
  scraped:   ScraperTargetAttribute,
  state:     ScraperStateChangeAttribute,
  data:      Option[Map[String, String]],
  targets:   Vector[ScraperTargetAttribute]
) extends LedgerEntry

case class ScraperTargetAdditionEntry(
  timestamp: Long,
  scraper:   ScraperTargetAttribute
) extends LedgerEntry

case class ScraperTargetRemovalEntry(
  timestamp: Long,
  target:    ScraperTargetAttribute
) extends LedgerEntry

case class ScraperStateChangeEntry(
  timestamp: Long,
  state: ScraperStateChangeAttribute
) extends LedgerEntry
