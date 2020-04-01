package com.yoxan.astraeus.config

import pureconfig.generic.semiauto._

case class AppConfig(server: ServerConfig, oauth: AuthenticationConfig)

object AppConfig {
  implicit val configReader = deriveReader[AppConfig]
}
