package com.yoxan.astraeus.route

import cats.effect.{ ContextShift, Sync }
import com.yoxan.astraeus.error.{ errorBody, ServerError }
import com.yoxan.astraeus.graphql.GraphQLResolver
import org.http4s.StaticFile
import tapir._
import tapir.server.ServerEndpoint

import scala.concurrent.ExecutionContext

class GraphQLBrowserRoute[F[_]: Sync: ContextShift](
    val graphQLResolver: GraphQLResolver[F, _]
)(
    implicit ec: ExecutionContext
) {
  def route: ServerEndpoint[_, _, _, Nothing, F] =
    endpoint
      .name("GraphQL browser route")
      .description("Graphql route")
      .get
      .in("browser")
      .errorOut(errorBody)
      .out(htmlBodyUtf8)
      .serverLogic[F](
        _ =>
          //TODO: Should be streaming
          StaticFile
            .fromResource[F]("/graphiql.html", ec, None)
            .semiflatMap(_.bodyAsText.compile.fold("")(_ + _))
            .toRight(
              ServerError.apply("Browser was not found", Some(2)).toDTO()
            )
            .value
      )
}
