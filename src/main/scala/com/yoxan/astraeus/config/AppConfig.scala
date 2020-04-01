package com.yoxan.astraeus.config

import pureconfig.generic.semiauto._

case class AppConfig(server: ServerConfig)

object AppConfig {
  implicit val configReader = deriveReader[AppConfig]
}
