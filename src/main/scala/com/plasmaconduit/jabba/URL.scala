package com.plasmaconduit.jabba

case class URL(protocol: WebProtocol,
               host: String,
               path: String)

sealed trait WebProtocol
case object Http  extends WebProtocol
case object Https extends WebProtocol
