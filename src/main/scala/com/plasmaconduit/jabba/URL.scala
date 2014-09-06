package com.plasmaconduit.jabba

import scala.util.parsing.combinator._

case class URL(protocol: String, host: String, port: Option[Int], relative: RelativeURL) {

  override def toString: String = {
    s"$protocol://$host${port.map(":" + _.toString).getOrElse("")}$toRequestPath"
  }

  def toBase: String = {
    s"$protocol://$host${port.map(":" + _.toString).getOrElse("")}"
  }

  def toRequestPath: String = {
    relative.toString
  }
}

case class RelativeURL(path: String, query: Option[String]) {

  override def toString: String = {
    s"$path${query.map("?" + _).getOrElse("")}"
  }

}

object URL extends RegexParsers {

  val protocol: Parser[String] = opt("[^:]+".r <~ "://") ^^ { _.getOrElse("http").toLowerCase }

  val host: Parser[String] = "[^:\\/]+".r

  val port: Parser[Option[Int]] = opt(":" ~> "[0-9]+".r) ^^ { _.map(_.toInt) }

  val path: Parser[String] = opt("/[^?]*".r) ^^ { _.getOrElse("/") }

  val query: Parser[Option[String]] = opt("?" ~> ".+".r)

  val relativeParser: Parser[RelativeURL] = path ~ query ^^ {
    case p ~ q => RelativeURL(p, q)
  }

  val fullParser: Parser[URL] = protocol ~ host ~ port ~ relativeParser ^^ {
    case pr ~ ho ~ po ~ re => URL(pr, ho, po, re)
  }

  def fromVector(urls: Vector[String]): Vector[URL] = {
    urls.flatMap(n => parseFull(n).toVector)
  }

  def parseFull(url: String): Option[URL] = {
    parseAll(fullParser, url) match {
      case NoSuccess(e, _)  => None
      case Success(u, _) => Some(u)
    }
  }

  def parseRelative(url: String): Option[RelativeURL] = {
    parseAll(relativeParser, url) match {
      case NoSuccess(e, _)  => None
      case Success(u, _) => Some(u)
    }
  }

  def canonicalize(base: URL, subject: String): Option[URL] = {
    parseFull(subject).orElse({ parseRelative(subject).map(n => base.copy(relative = n)) })
  }

}

object URLs {

  def apply(urls: String*): Vector[URL] = {
    urls.flatMap(n => URL.parseFull(n).toVector).toVector
  }

}
