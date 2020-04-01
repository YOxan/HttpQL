package com.yoxan.astraeus.config

import pureconfig.generic.semiauto._

case class AuthenticationConfig(
    key: String,
    issuer: String,
    audience: String
)

object AuthenticationConfig {
  implicit val configReader = deriveReader[AuthenticationConfig]
}
