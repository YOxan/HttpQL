package com.yoxan.astraeus.graphql

import cats.data.OptionT
import cats.effect.Sync
import com.yoxan.astraeus.error.ProfileNotFound

class GraphQLContext[F[_]: Sync]() {}

object GraphQLContext {
  type Builder[T <: GraphQLContext[F], F[_]] = () => F[T]
}
