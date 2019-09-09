package com.yoxan.astraeus.route

import cats.data.{ Kleisli, OptionT }
import cats.effect.{ ContextShift, Sync }
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{ HttpRoutes, Request, Response }
import tapir.docs.openapi._
import tapir.openapi.circe.yaml._
import tapir.server.ServerEndpoint
import tapir.server.http4s._
import tapir.swagger.http4s.SwaggerHttp4s

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

  val openApiYaml =
    endpointsList.map(_.endpoint).toOpenAPI("Contacts API", "v1.0").toYaml

  val route = {
    val swaggerRoutes = Router(
      "/docs" -> new SwaggerHttp4s(openApiYaml).routes[F]
    )

    val routes: HttpRoutes[F] = Kleisli[OptionT[F, ?], Request[F], Response[F]] { req: Request[F] =>
      endpointsList.toRoutes
        .apply(req)
        .orElse(swaggerRoutes.apply(req))
    }

    routes.orNotFound
  }

}
