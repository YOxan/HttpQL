package com.yoxan.astraeus

import cats.Functor
import cats.effect.{ ConcurrentEffect, IO, Timer }
import cats.implicits._
import com.yoxan.astraeus.config.{ ConfigLoader, ServerConfig }
import com.yoxan.astraeus.route.Api
import org.http4s.server.blaze.BlazeServerBuilder

class Server[F[_]: ConcurrentEffect: Timer: Functor](val serverConfigF: F[ServerConfig]) {
  def start(apiV1: Api[F]) =
    serverConfigF.flatMap(
      cfg =>
        BlazeServerBuilder[F]
          .bindHttp(cfg.port, cfg.host)
          .withHttpApp(apiV1.route)
          .resource
          .use(_ => ConcurrentEffect[F].never[Unit])
    )
}

object Server {
  def apply[F[_]](implicit ce: ConcurrentEffect[F], timer: Timer[F]) = {
    val httpCfg = ConfigLoader.loadServerConfig[F]

    new Server[F](httpCfg)
  }
}
