package com.yoxan.astraeus.route

import cats.data.{ Kleisli, OptionT }
import cats.effect.{ ContextShift, Effect }
import javax.inject.Inject
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{ HttpRoutes, Request, Response }
import tapir.docs.openapi._
import tapir.openapi.circe.yaml._
import tapir.server.ServerEndpoint
import tapir.server.http4s._
import tapir.swagger.http4s.SwaggerHttp4s

class ApiV1[F[_]] @Inject()(
    val graphQLRoute: GraphQLRoute[F],
    val graphQLBrowserRoute: GraphQLBrowserRoute[F],
    val getSDLRoute: GetSDLRoute[F]
)(
    implicit val cs: ContextShift[F],
    //FIXME: Fix it shouldn't be here
    val E: Effect[F]
) {

  val endpointsList: List[ServerEndpoint[_, _, _, Nothing, F]] =
    List(
      graphQLRoute.route,
      graphQLBrowserRoute.route,
      getSDLRoute.route
    )
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
