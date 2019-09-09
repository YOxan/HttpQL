package com.yoxan.astraeus.route

import cats.effect.{ Async, ContextShift }
import com.yoxan.astraeus.error.errorBody
import com.yoxan.astraeus.error.ServerError
import javax.inject.Inject
import org.http4s.StaticFile
import tapir._
import tapir.server.ServerEndpoint

import scala.concurrent.ExecutionContext

class GraphQLBrowserRoute[F[_]] @Inject()(
    val graphQLResolver: GraphQLResolver[F]
)(
    implicit val cs: ContextShift[F],
    ec: ExecutionContext,
    //FIXME: Shouldn't be here
    FAsync: Async[F]
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
