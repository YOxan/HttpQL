package com.yoxan.astraeus.route

import cats.effect.{ Async, ContextShift, Effect }
import cats.implicits._
import com.yoxan.astraeus.error.errorBody
import com.yoxan.astraeus.error.ErrorDTO
import javax.inject.Inject
import tapir.server.ServerEndpoint
import tapir.{ auth, endpoint, stringBody }

class GetSDLRoute[F[_]] @Inject()(
    val graphQLResolver: GraphQLResolver[F],
    val authorization: Authorization[F],
    val schemaDefinition: SchemaDefinition[F]
)(
    implicit val cs: ContextShift[F],
    A: Async[F]
) extends BaseRoute[F] {

  def route(implicit E: Effect[F]): ServerEndpoint[_, _, _, Nothing, F] =
    endpoint
      .name("GraphQL SDL route")
      .description("Graphql SDL route")
      .get
      .in("sdl")
      .in(auth.bearer)
      .errorOut(errorBody)
      .out(stringBody)
      .serverLogic[F](
        _ =>
          Either
            .right[ErrorDTO, String](schemaDefinition.render)
            .toEitherT[F]
            .value
      )
}
