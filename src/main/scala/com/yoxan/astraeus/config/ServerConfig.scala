package com.yoxan.astraeus.config

import pureconfig.generic.semiauto._

case class ServerConfig(host: String, port: Int)

object ServerConfig {
  implicit val configReader = deriveReader[ServerConfig]

  val default = ServerConfig("localhost", 8080)
}
