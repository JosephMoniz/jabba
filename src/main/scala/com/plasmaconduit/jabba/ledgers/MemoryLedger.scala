package com.plasmaconduit.jabba.ledgers

import com.plasmaconduit.jabba._
import rx.lang.scala._

case class MemoryLedger(events: Vector[LedgerEntry] = Vector()) extends Ledger {

  def record(entry: LedgerEntry): Observable[Ledger] = {
    Observable.create({(observer) =>
      observer.onNext(MemoryLedger(events :+ entry))
      observer.onCompleted()
      Subscription()
    })
  }

  def readFromRecent: Observable[LedgerRead] = {
    Observable.create({(observer) =>
      observer.onNext(LedgerRead(MemoryLedger(), Observable.from(events)))
      observer.onCompleted()
      Subscription()
    })
  }

  def readFromBeginning: Observable[LedgerRead] = {
    Observable.create({(observer) =>
      observer.onNext(LedgerRead(MemoryLedger(), Observable.from(events)))
      observer.onCompleted()
      Subscription()
    })
  }

}
