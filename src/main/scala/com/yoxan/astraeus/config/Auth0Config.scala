package com.yoxan.astraeus.config

import pureconfig.generic.semiauto._

case class Auth0Config(
    key: String,
    issuer: String,
    audience: String
)
object Auth0Config {
  implicit val configReader = deriveReader[Auth0Config]
}
