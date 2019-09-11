package com.yoxan.astraeus.user

import java.time.Clock

import cats._
import cats.data.EitherT
import cats.effect.Effect
import cats.implicits._
import com.yoxan.astraeus.config.AuthenticationConfig
import com.yoxan.astraeus.error.NotAuthorized
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim }

class Authorization[F[_]: Applicative: Effect](authConfig: AuthenticationConfig) {

  implicit val clock = Clock.systemDefaultZone()

  val key = authConfig.key
  val alg = JwtAlgorithm.HS256

  val issuer   = authConfig.issuer
  val audience = authConfig.audience

  def decodeJwt(token: String): F[JwtClaim] =
    Effect[F].fromTry(
      JwtCirce.decode(token, key, Seq(alg)).ensure(NotAuthorized)(_.isValid(issuer, audience))
    )

  def getId(token: String): F[Either[Throwable, String]] =
    decodeJwt(token).attemptT
      .flatMap(jwt => EitherT.fromOption[F].apply[Throwable, String](jwt.subject, NotAuthorized))
      .value

}
