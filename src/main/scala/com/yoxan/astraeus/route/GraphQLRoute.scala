package com.yoxan.astraeus.route

import cats.Monad
import cats.data.EitherT
import cats.effect.Async
import cats.syntax.option._
import com.yoxan.astraeus.error.{ errorBody, ServerError }
import com.yoxan.astraeus.graphql.{ GraphQLContext, GraphQLResolver }
import com.yoxan.astraeus.user.{ Authorization, UserProvider }
import io.circe.generic.auto._
import sangria.marshalling.circe
import tapir._
import tapir.json.circe._
import tapir.server.ServerEndpoint

class GraphQLRoute[F[_]: Async: Monad, T <: GraphQLContext[F, String]](
    val graphQLResolver: GraphQLResolver[F, T],
    val contextBuilder: GraphQLContext.Builder[T, F, String],
    val authorization: Authorization[F],
    val userProvider: UserProvider[F, String]
) extends BaseRoute[F] {

  def route: ServerEndpoint[_, _, _, Nothing, F] =
    endpoint
      .name("GraphQL route")
      .description("Graphql route")
      .post
      .in("graphql")
      .in(auth.bearer)
      .in(jsonBody[Query])
      //TODO: Check why error doesn't work and is shown only at console
      .errorOut(errorBody)
      .out(jsonBody[circe.CirceResultMarshaller.Node])
      .serverLogic[F] {
        case (jwt, query) =>
          EitherT[F, Throwable, String](authorization.getId(jwt))
            .map(userId => contextBuilder(userId.some, userProvider))
            .flatMap(graphQLResolver.execute(_, query))
            .leftMap(ex => ServerError.toError(ex).toDTO())
            .value
      }
}
