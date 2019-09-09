package com.yoxan.astraeus.route

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, Effect }
import com.contact4u.domain.graphql.GraphQLResolver
import com.contact4u.error.errorBody
import com.yoxan.astraeus.error.ServerError
import io.circe.generic.auto._
import javax.inject.Inject
import tapir._
import tapir.json.circe._
import tapir.server.ServerEndpoint

class GraphQLRoute[F[_]] @Inject()(
    val graphQLResolver: GraphQLResolver[F],
    val authorization: Authorization[F]
)(
    implicit val cs: ContextShift[F],
    A: Async[F]
) extends BaseRoute[F] {

  def route(implicit E: Effect[F]): ServerEndpoint[_, _, _, Nothing, F] =
    endpoint
      .name("GraphQL route")
      .description("Graphql route")
      .post
      .in("graphql")
      .in(auth.bearer)
      .in(jsonBody[Query])
      //TODO: Check why error doesn't work and is shown only at console
      .errorOut(errorBody)
      .out(stringBody)
      .serverLogic[F] {
        case (jwt, query) =>
          EitherT[F, Throwable, String](authorization.getId(jwt))
            .flatMapF(graphQLResolver.execute(_, query))
            .leftMap(ex => ServerError.toError(ex).toDTO())
            .value
      }
}
