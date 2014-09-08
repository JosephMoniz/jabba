package com.plasmaconduit.jabba

import rx.lang.scala._

trait Ledger {
  def record(entry: LedgerEntry): Observable[Ledger]
  def readFromBeginning: Observable[LedgerRead]
  def readFromRecent: Observable[LedgerRead]
}

final case class LedgerRead(ledger: Ledger, records: Observable[LedgerEntry])

final case class ScraperTargetAttribute(scraper: String, url: URL)

final case class ScraperStateChangeAttribute(scraper: String,
                                             state: ScraperActivityState)

sealed trait LedgerEntry

final case class ScraperSuccessEntry(
  timestamp: Long,
  scraped:   ScraperTargetAttribute,
  state:     ScraperStateChangeAttribute,
  data:      Option[Map[String, String]],
  targets:   Vector[ScraperTargetAttribute]
) extends LedgerEntry

final case class ScraperFailureEntry(
  timestamp: Long,
  scraped: ScraperTargetAttribute,
  reason: String
) extends LedgerEntry

final case class ScraperTargetAdditionEntry(
  timestamp: Long,
  scraper:   ScraperTargetAttribute
) extends LedgerEntry

final case class ScraperTargetRemovalEntry(
  timestamp: Long,
  target:    ScraperTargetAttribute
) extends LedgerEntry

final case class ScraperStateChangeEntry(
  timestamp: Long,
  state: ScraperStateChangeAttribute
) extends LedgerEntry
