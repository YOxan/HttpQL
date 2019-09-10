package com.yoxan.astraeus.config

import cats.effect.Sync
import com.typesafe.config.ConfigFactory

import scala.util.Try

object ConfigLoader {

  private val config = ConfigFactory.load("server.conf")

  def loadServerConfig[F[_]: Sync]: F[ServerConfig] =
    Sync[F].fromTry[ServerConfig](Try {
      pureconfig.loadConfigOrThrow[ServerConfig](config, "server")
    })

  def loadAuth0Config[F[_]: Sync]: F[Auth0Config] =
    Sync[F].fromTry(Try {
      pureconfig.loadConfigOrThrow[ServerConfig](config, "auth0")
    })
}
