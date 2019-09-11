package com.yoxan.astraeus.route

import cats.effect.Async
import cats.implicits._
import com.yoxan.astraeus.error.{ errorBody, ErrorDTO }
import com.yoxan.astraeus.graphql.SchemaDefinition
import com.yoxan.astraeus.user.Authorization
import tapir.server.ServerEndpoint
import tapir.{ auth, endpoint, stringBody }

class GetSDLRoute[F[_]: Async](
    val authorization: Authorization[F],
    val schemaDefinition: SchemaDefinition[_]
) extends BaseRoute[F] {

  def route: ServerEndpoint[_, _, _, Nothing, F] =
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
