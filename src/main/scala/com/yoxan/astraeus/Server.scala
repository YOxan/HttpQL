package com.yoxan.astraeus

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import cats.implicits._
import com.yoxan.astraeus.config.{ AuthenticationConfig, ConfigLoader, ServerConfig }
import com.yoxan.astraeus.graphql.{ GraphQLContext, SchemaDefinition }
import com.yoxan.astraeus.route.Api
import com.yoxan.astraeus.user.UserProvider
import org.http4s.server.blaze.BlazeServerBuilder
import sangria.execution.deferred.DeferredResolver
import tapir.server.ServerEndpoint

import scala.concurrent.ExecutionContext

class Server[F[_]: ConcurrentEffect: Timer: ContextShift](
    val serverConfigF: F[ServerConfig],
    val authConfigF: F[AuthenticationConfig]
) {
  def start(apiV1: Api[F]): F[Unit] =
    serverConfigF.flatMap(
      cfg =>
        BlazeServerBuilder[F]
          .bindHttp(cfg.port, cfg.host)
          .withHttpApp(apiV1.route)
          .resource
          .use(_ => ConcurrentEffect[F].never[Unit])
    )

  def start(
      schemaDefinition: SchemaDefinition[GraphQLContext[F, String]],
      resolver: DeferredResolver[GraphQLContext[F, String]],
      userProvider: UserProvider[F, String],
      additionalRoutes: List[ServerEndpoint[_, _, _, Nothing, F]] = List.empty
  )(
      implicit ec: ExecutionContext
  ): F[Unit] =
    authConfigF
      .map(
        Api.apply[F](schemaDefinition, resolver, userProvider, _, additionalRoutes)
      )
      .flatMap(start)
}

object Server {
  def apply[F[_]: ContextShift](implicit ce: ConcurrentEffect[F], timer: Timer[F]) = {
    val httpCfg = ConfigLoader.loadServerConfig[F]
    val authCfg = ConfigLoader.loadAuthenticationConfig[F]

    new Server[F](httpCfg, authCfg)
  }
}
