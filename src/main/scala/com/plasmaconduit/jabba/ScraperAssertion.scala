package com.plasmaconduit.jabba

import scala.util._

sealed trait ScraperAssertion {
  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess]
}

object ScraperAssertion {

  def run(machine: ScraperStateMachine, result: ScraperResult): ScraperResult = {
    result match {
      case success @ ScraperSuccess(_, _, _, _) =>
        machine.assertions.assert(success).fold(identity, identity)
      case failure @ ScraperFailure(_, _, _) =>
        failure
    }

  }

}

final case class ScraperAssertions(assertions: Vector[ScraperAssertion]) extends ScraperAssertion {

  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess] = {
    assertions.foldLeft[Either[ScraperFailure, ScraperSuccess]](Right(result)) {(s, a) =>
      s.right.flatMap(r => a.assert(r))
    }
  }

}

object ScraperAssertions {

  def apply(assertions: ScraperAssertion*): ScraperAssertions = {
    ScraperAssertions(assertions.toVector)
  }

}

case object NilScraperAssertion extends ScraperAssertion {

  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess] = {
    Right(result)
  }

}

case object MustContainData extends ScraperAssertion {

  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess] = {
    result.data match {
      case Some(n) => Right(result)
      case None    => Left(ScraperFailure(
        url     = result.url,
        scraper = result.scraper,
        reason  = "Results must contain data")
      )
    }
  }

}

case object MustContainTargets extends ScraperAssertion {

  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess] = {
    result.targets.headOption match {
      case Some(n) => Right(result)
      case None    => Left(ScraperFailure(
        url     = result.url,
        scraper = result.scraper,
        reason  = "Results must contain targets"
      ))
    }
  }

}

final case class MustContainTargetsFor(machine: ScraperStateMachine) extends ScraperAssertion {

  def assert(result: ScraperSuccess): Either[ScraperFailure, ScraperSuccess] = {
    result.targets.find(n => n.scraper.name == machine.name) match {
      case Some(n) => Right(result)
      case None    => Left(ScraperFailure(
        url = result.url,
        scraper = result.scraper,
        reason = s"Result must contain a target for ${machine.name}"
      ))
    }
  }

}