package com.yoxan.astraeus.graphql

import cats.data.OptionT
import cats.effect.Sync
import com.yoxan.astraeus.error.ProfileNotFound
import com.yoxan.astraeus.user.{ Identifiable, UserProvider }

class GraphQLContext[F[_]: Sync, IdType](val user: Identifiable[IdType]) {}

object GraphQLContext {
  type Builder[T <: GraphQLContext[F, IdType], F[_], IdType] = (Option[IdType], UserProvider[F, IdType]) => F[T]
}
