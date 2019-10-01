package com.yoxan.astraeus.config

import cats.effect.Sync
import cats.implicits._
import com.typesafe.config.Config

import scala.util.Try

object ConfigLoader {

  def loadAppConfig[F[_]: Sync](configF: F[Config]) =
    configF.flatMap(
      config =>
        Sync[F].fromTry(Try {
          pureconfig.loadConfigOrThrow[AppConfig](config)
        })
    )
}
