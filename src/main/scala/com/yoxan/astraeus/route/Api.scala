package com.yoxan.astraeus.route

import cats.data.{ Kleisli, OptionT }
import cats.effect.{ ContextShift, Effect, Sync }
import com.yoxan.astraeus.config.AuthenticationConfig
import com.yoxan.astraeus.graphql.{ GraphQLContext, GraphQLResolver, SchemaDefinition }
import com.yoxan.astraeus.user.{ Authorization, UserProvider }
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{ HttpRoutes, Request, Response }
import sangria.execution.deferred.DeferredResolver
import tapir.docs.openapi._
import tapir.openapi.circe.yaml._
import tapir.server.ServerEndpoint
import tapir.server.http4s._
import tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext

class Api[F[_]: Sync: ContextShift](
    val graphQLRoute: GraphQLRoute[F],
    val graphQLBrowserRoute: GraphQLBrowserRoute[F],
    val getSDLRoute: GetSDLRoute[F],
    val additionalRoutes: List[ServerEndpoint[_, _, _, Nothing, F]] = List.empty
) {

  val endpointsList: List[ServerEndpoint[_, _, _, Nothing, F]] =
    List(
      graphQLRoute.route,
      graphQLBrowserRoute.route,
      getSDLRoute.route
    ) ++ additionalRoutes

  //TODO: Should be in the config file
  val openApiYaml = endpointsList.map(_.endpoint).toOpenAPI("Contacts API", "v1.0").toYaml

  val route = {
    val swaggerRoutes = Router(
      "/docs" -> new SwaggerHttp4s(openApiYaml).routes[F]
    )

    val routes: HttpRoutes[F] = Kleisli[OptionT[F, *], Request[F], Response[F]] { req: Request[F] =>
      endpointsList.toRoutes
        .apply(req)
        .orElse(swaggerRoutes.apply(req))
    }

    routes.orNotFound
  }
}

object Api {
  def apply[F[_]: Effect: ContextShift](
      schemaDefinition: SchemaDefinition[GraphQLContext[F, String]],
      resolver: DeferredResolver[GraphQLContext[F, String]],
      userProvider: UserProvider[F, String],
      authenticationConfig: AuthenticationConfig,
      additionalRoutes: List[ServerEndpoint[_, _, _, Nothing, F]] = List.empty
  )(
      implicit ec: ExecutionContext
  ) = {
    val graphQLResolver = new GraphQLResolver[F, GraphQLContext[F, String]](resolver, schemaDefinition)
    val authorization   = new Authorization[F](authenticationConfig)

    val graphQLRoute        = new GraphQLRoute[F](graphQLResolver, authorization, userProvider)
    val graphQLBrowserRoute = new GraphQLBrowserRoute[F](graphQLResolver)
    val getSDLRoute         = new GetSDLRoute[F](authorization, schemaDefinition)

    new Api[F](graphQLRoute, graphQLBrowserRoute, getSDLRoute, additionalRoutes)
  }
}
