package com.yoxan.astraeus.graphql

import cats.data.OptionT
import cats.effect.Sync
import com.yoxan.astraeus.error.ProfileNotFound
import com.yoxan.astraeus.user.{ Identifiable, UserProvider }

class GraphQLContext[F[_]: Sync, IdType](
    val optIdentifier: Option[IdType],
    val userProvider: UserProvider[F, IdType]
) {
  lazy val user: F[Identifiable[IdType]] = OptionT
    .fromOption[F](optIdentifier)
    .flatMapF(userProvider.getUserById)
    .getOrElseF(Sync[F].raiseError[Identifiable[IdType]](ProfileNotFound))
}
