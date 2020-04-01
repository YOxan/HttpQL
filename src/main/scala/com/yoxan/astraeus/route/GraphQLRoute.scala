package com.yoxan.astraeus.route

import cats.implicits._
import cats.Monad
import cats.data.EitherT
import cats.effect.Async
import cats.syntax.option._
import com.yoxan.astraeus.error.{ errorBody, ServerError }
import com.yoxan.astraeus.graphql.{ GraphQLContext, GraphQLResolver }
import io.circe.generic.auto._
import sangria.marshalling.circe
import tapir._
import tapir.json.circe._
import tapir.server.ServerEndpoint
import cats.FlatMap

class GraphQLRoute[F[_]: Async: Monad, T <: GraphQLContext[F]](
    val graphQLResolver: GraphQLResolver[F, T],
    val contextBuilder: GraphQLContext.Builder[T, F]
) extends BaseRoute[F] {

  def route: ServerEndpoint[_, _, _, Nothing, F] =
    endpoint
      .name("GraphQL route")
      .description("Graphql route")
      .post
      .in("graphql")
      .in(jsonBody[Query])
      //TODO: Check why error doesn't work and is shown only at console
      .errorOut(errorBody)
      .out(jsonBody[circe.CirceResultMarshaller.Node])
      .serverLogic[F] { query =>
        EitherT
          .liftF(contextBuilder())
          .flatMap(ctx => graphQLResolver.execute(ctx, query))
          .leftMap(ex => ServerError.toError(ex).toDTO())
          .value
      }
}
