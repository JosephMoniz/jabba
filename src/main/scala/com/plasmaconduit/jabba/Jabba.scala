package com.plasmaconduit.jabba

import rx.lang.scala._
import scala.collection.immutable._
import scala.concurrent.duration._

final case class Jabba(transactor: Transactor,
                       initialLedger: Ledger,
                       initialScrapers: Scrapers)
{

  def scrape(): Unit = {
    val prepped = prepLedger(initialLedger, initialScrapers).toBlocking.head
    val resumed = resumeState(prepped, initialScrapers).toBlocking.head
    var state   = resumed.scrapers
    var ledger  = resumed.ledger
    while (true) {
      val update = updateState(ledger, state).flatMap(updated =>
        transactor
          .transactReadyScrapers(updated.scrapers.readyScrapers)
          .foldLeft(Observable.just(updated.ledger)) {(o, e) =>
            o.flatMap(l => l.record(e.toLedgerEntry))
          }
          .flatten
          .map(l => ScraperStateUpdate(l, updated.scrapers))
      ).toBlocking.head
      state  = update.scrapers
      ledger = update.ledger
      Thread.sleep(1.seconds.toMillis)
    }
  }

  def prepLedger(ledger: Ledger, scrapers: Scrapers): Observable[Ledger] = {
    scrapers.initialUrls.foldLeft(Observable.just(ledger)) {(o, u) =>
      o.flatMap(l => l.record(u.toLedgerAdditionEntry))
    }.flatten
  }

  def resumeState(ledger: Ledger, scrapers: Scrapers): Observable[ScraperStateUpdate] = for (
    result <- ledger.readFromBeginning;
    state  <- applyEventsToScraperState(scrapers, result.records)
  ) yield ScraperStateUpdate(result.ledger, state)

  def updateState(ledger: Ledger, scrapers: Scrapers): Observable[ScraperStateUpdate] = for (
    result <- ledger.readFromRecent;
    state  <- applyEventsToScraperState(scrapers, result.records)
  ) yield ScraperStateUpdate(result.ledger, state)

  def applyEventsToScraperState(scrapers: Scrapers, events: Observable[LedgerEntry]): Observable[Scrapers] = {
    events.foldLeft(scrapers) {(state, entry) => entry match {
      case ScraperTargetAdditionEntry(_, target) =>
        state.addTargetToQueue(target)
      case ScraperTargetRemovalEntry(_, target) =>
        state.removeTargetFromQueue(target)
      case ScraperStateChangeEntry(_, change) =>
        state.transitionScraperState(change)
      case ScraperFailureEntry(_, target, _) =>
        state.markTargetAsScraped(target)
      case ScraperSuccessEntry(_, scraped, next, data, targets) =>
        state
          .transitionScraperState(next)
          .markTargetAsScraped(scraped)
          .addMultipleTargetsToQueue(targets)
    }}
  }

}

object Jabba {

  def fromStateMachines(transactor: Transactor, ledger: Ledger, scrapers: Vector[ScraperStateMachine]): Jabba = {
    Jabba(transactor, ledger, Scrapers.fromStateMachineVector(scrapers))
  }

}

final case class ScraperStateUpdate(ledger: Ledger, scrapers: Scrapers)