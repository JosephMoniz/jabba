package com.plasmaconduit.jabba

import scala.util.parsing.combinator._

case class Url(protocol: String, host: String, port: Option[Int], relative: RelativeUrl) {

  override def toString: String = {
    s"$protocol://$host${port.map(":" + _.toString).getOrElse("")}$toRequestPath"
  }

  def toBase: String = {
    s"$protocol://$host${port.map(":" + _.toString)}"
  }

  def toRequestPath: String = {
    relative.toString
  }
}

case class RelativeUrl(path: String, query: Option[String]) {

  override def toString: String = {
    s"$path${query.getOrElse("")}"
  }

}

object Url extends RegexParsers {

  val protocol: Parser[String] = opt("[^:]+".r <~ "://") ^^ { _.getOrElse("http").toLowerCase }

  val host: Parser[String] = "[^:\\/]+".r

  val port: Parser[Option[Int]] = opt(":" ~> "[0-9]+".r) ^^ { _.map(_.toInt) }

  val path: Parser[String] = opt("/[^?]*".r) ^^ { _.getOrElse("/") }

  val query: Parser[Option[String]] = opt("?" ~> ".+".r)

  val relativeParser: Parser[RelativeUrl] = path ~ query ^^ {
    case p ~ q => RelativeUrl(p, q)
  }

  val fullParser: Parser[Url] = protocol ~ host ~ port ~ relativeParser ^^ {
    case pr ~ ho ~ po ~ re => Url(pr, ho, po, re)
  }

  def fromVector(urls: Vector[String]): Vector[Url] = {
    urls.flatMap(n => parseFull(n).toVector)
  }

  def parseFull(url: String): Option[Url] = {
    parseAll(fullParser, url) match {
      case NoSuccess(e)  => println(url);println(e); None
      case Success(u, _) => Some(u)
    }
  }

  def parseRelative(url: String): Option[RelativeUrl] = {
    parseAll(relativeParser, url) match {
      case NoSuccess(e)  => println(url);println(e); None
      case Success(u, _) => Some(u)
    }
  }

  def canonicalize(base: Url, subject: String): Option[Url] = {
    parseFull(subject).orElse({ parseRelative(subject).map(n => base.copy(relative = n)) })
  }

}
