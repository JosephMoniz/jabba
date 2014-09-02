package com.plasmaconduit.jabba

import rx.lang.scala._

case class Transactor(factory: BrowserFactory) {

  def transactReadyScrapers(obs: Observable[ScraperState]): Observable[ScraperResult] = {
    obs.flatMap(n => transactScraper(n))
  }

  def transactScraper(scraper: ScraperState): Observable[ScraperResult] = {
    val running = scraper.stateMachine.running
    val state   = scraper.stateMachine.toState(Running)
    for (
      url     <- scraper.nextUrlFromQueue;
      _       <- printScraperState(url, scraper.stateMachine);
      browser <- factory.open;
      page    <- browser.visit(url);
      result  <- Observable.just(running.scrape(state, url, page.document));
      _       <- page.close;
      _       <- printScraperResults(scraper.stateMachine, result)
    ) yield result
  }

  def printScraperState(url: String, stateMachine: ScraperStateMachine): Observable[Unit] = {
    Observable.create({observer =>
      println(s"Running ${stateMachine.name}:")
      println(s"  + previous state: ${stateMachine.current.state}")
      println(s"  + url: $url")
      observer.onNext(Unit)
      observer.onCompleted()
      Subscription()
    })
  }

  def printScraperResults(stateMachine: ScraperStateMachine, result: ScraperResult): Observable[Unit] = {
    Observable.create({(observer) =>
      println(s"Results for ${stateMachine.name}")
      println(s"  + next state: ${result.scraper.current.state}")
      if (result.targets.length > 0) {
        println("  + links:")
        result.targets.map(n => println(s"    - ${n.scraper.name} <- ${n.url}"))
      } else {
        println("  + links: None")
      }
      if (result.data.isEmpty) {
        println("  + data: None")
      } else {
        println("  + data:")
        println(result.data.get)
      }
      observer.onNext(Unit)
      observer.onCompleted()
      Subscription()
    })
  }

}
