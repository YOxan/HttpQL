package com.yoxan.astraeus.user

trait UserProvider[F[_], IdType] {
  def getUserById(id: IdType): F[Option[Identifiable[IdType]]]
}
