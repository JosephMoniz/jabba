package com.plasmaconduit.jabba

import rx.lang.scala._

case class Transactor(factory: BrowserFactory) {

  def transactReadyScrapers(obs: Observable[ScraperState]): Observable[ScraperResult] = for (
    scraper <- obs;
    url     <- scraper.nextUrlFromQueue;
    _       <- printScraperState(url, scraper.stateMachine);
    result  <- transactScraper(scraper, url);
    _       <- printScraperResults(scraper.stateMachine, result)
  ) yield result


  def transactScraper(scraper: ScraperState, url: URL): Observable[ScraperResult] = {
    val running = scraper.stateMachine.running
    val state   = scraper.stateMachine.toState(Running)
    for (
      browser <- factory.open;
      page    <- browser.visit(url);
      result  <- Observable.just(running.scrape(state, url, page.document));
      _       <- page.close
    ) yield ScraperAssertion.run(state, result)
  }

  def printScraperState(url: URL, stateMachine: ScraperStateMachine): Observable[Unit] = {
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
      result match {
        case s @ ScraperSuccess(_, _, _, _) =>
          println("  + result: Success")
          println(s"  + next state: ${s.scraper.current.state}")
          if (s.targets.length > 0) {
            println("  + links:")
            s.targets.map(n => println(s"    - ${n.scraper.name} <- ${n.url}"))
          } else {
            println("  + links: None")
          }
          if (s.data.isEmpty) {
            println("  + data: None")
          } else {
            println("  + data:")
            println(s.data.get)
          }
        case f @ ScraperFailure(_, _, _) =>
          println("  + result: Failure")
          println(s"  + reason: ${f.reason}")
      }
      observer.onNext(Unit)
      observer.onCompleted()
      Subscription()
    })
  }

}
