package com.yoxan.astraeus

import cats.effect.{ ConcurrentEffect, ContextShift, Timer }
import cats.implicits._
import com.typesafe.config.Config
import com.yoxan.astraeus.config.{ AppConfig, ConfigLoader }
import com.yoxan.astraeus.graphql.{ GraphQLContext, SchemaDefinition }
import com.yoxan.astraeus.route.Api
import com.yoxan.astraeus.user.UserProvider
import javax.net.ssl.SSLContext
import org.http4s.server.blaze.BlazeServerBuilder
import sangria.execution.deferred.DeferredResolver
import tapir.server.ServerEndpoint

import scala.concurrent.ExecutionContext

class Server[F[_]: ConcurrentEffect: Timer: ContextShift, T <: GraphQLContext[F, String]](
    val appConfigF: F[AppConfig]
) {
  def start(apiV1: Api[F, T], sslContext: Option[SSLContext]): F[Unit] =
    appConfigF
      .map(_.server)
      .flatMap(
        cfg => {
          val builder = BlazeServerBuilder[F]
            .bindHttp(cfg.port, cfg.host)
            .withHttpApp(apiV1.route)

          sslContext
            .fold(builder)(builder.withSSLContext(_))
            .resource
            .use(_ => ConcurrentEffect[F].never[Unit])
        }
      )

  def start(
      schemaDefinition: SchemaDefinition[T],
      resolver: DeferredResolver[GraphQLContext[F, String]],
      userProvider: UserProvider[F, String],
      contextBuilder: GraphQLContext.Builder[T, F, String],
      additionalRoutes: List[ServerEndpoint[_, _, _, Nothing, F]] = List.empty,
      sslContext: Option[SSLContext] = None
  )(
      implicit ec: ExecutionContext
  ): F[Unit] = {
    val api = Api
      .apply[F, T](schemaDefinition, resolver, userProvider, appConfigF.map(_.oauth), contextBuilder, additionalRoutes)

    start(api, sslContext)
  }
}

object Server {
  def apply[F[_]: ContextShift, T <: GraphQLContext[F, String]](
      cfgF: F[Config]
  )(implicit ce: ConcurrentEffect[F], timer: Timer[F]) = {
    val appConfigF = ConfigLoader.loadAppConfig[F](cfgF)
    new Server[F, T](appConfigF)
  }
}
