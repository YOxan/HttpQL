package com.yoxan.astraeus

import cats.Functor
import cats.effect.{ ConcurrentEffect, Timer }
import cats.implicits._
import com.contact4u.config.ServerConfig
import com.contact4u.route.ApiV1
import com.yoxan.astraeus.config.ServerConfig
import com.yoxan.astraeus.route.ApiV1
import org.http4s.server.blaze.BlazeServerBuilder

class Server[F[_]: ConcurrentEffect: Timer: Functor](val serverConfigF: F[ServerConfig]) {
  def start(apiV1: ApiV1[F]) =
    serverConfigF.flatMap(
      cfg =>
        BlazeServerBuilder[F]
          .bindHttp(cfg.port, cfg.host)
          .withHttpApp(apiV1.route)
          .resource
          .use(_ => ConcurrentEffect[F].never[Unit])
    )
}
