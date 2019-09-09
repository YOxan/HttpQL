package com.yoxan.astraeus.config

import cats.effect.{ Effect, Sync }
import com.typesafe.config.ConfigFactory

import scala.util.Try

object ConfigLoader {

  private val config = ConfigFactory.load("server.conf")

  def loadServerConfig[F[_]: Sync: Effect]: F[ServerConfig] =
    Sync[F].fromTry[ServerConfig](Try {
      pureconfig.loadConfigOrThrow[ServerConfig](config, "server")
    })

}
