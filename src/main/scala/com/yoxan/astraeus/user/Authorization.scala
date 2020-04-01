package com.yoxan.astraeus.user

import java.time.Clock

import cats._
import cats.data.EitherT
import cats.effect.{ Effect, Sync }
import cats.implicits._
import com.yoxan.astraeus.config.AuthenticationConfig
import com.yoxan.astraeus.error.NotAuthorized
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim }

class Authorization[F[_]: Applicative: Effect](authConfig: F[AuthenticationConfig]) {

  implicit val clock = Clock.systemDefaultZone()

  val keyF = authConfig.map(_.key)
  val algF = Sync[F].pure(JwtAlgorithm.HS256)

  val issuerF   = authConfig.map(_.issuer)
  val audienceF = authConfig.map(_.audience)

  def decodeJwt(token: String): F[JwtClaim] =
    (keyF, algF, issuerF, audienceF)
      .mapN(
        (key, alg, issuer, audience) =>
          Effect[F].fromTry(JwtCirce.decode(token, key, Seq(alg)).ensure(NotAuthorized)(_.isValid(issuer, audience)))
      )
      .flatten

  def getId(token: String): F[Either[Throwable, String]] =
    decodeJwt(token).attemptT
      .flatMap(jwt => EitherT.fromOption[F].apply[Throwable, String](jwt.subject, NotAuthorized))
      .value

}
