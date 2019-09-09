package com.yoxan.astraeus.route

import java.time.Clock

import cats.data.EitherT
import cats.effect.Effect
import cats.implicits._
import cats._
import com.yoxan.astraeus.error.NotAuthorized
import javax.inject.Inject
import pdi.jwt.{ JwtAlgorithm, JwtCirce, JwtClaim }

class Authorization[F[_]: Applicative] @Inject()(implicit val E: Effect[F]) {

  implicit val clock = Clock.systemDefaultZone()

  val key = "DQTgBwu5q8142hvEkqfzslFJgyDtHPhi"
  val alg = JwtAlgorithm.HS256

  val issuer   = "https://contact-dream-team.eu.auth0.com/"
  val audience = "https://contact4u.ru"

  def decodeJwt(token: String): F[JwtClaim] =
    E.fromTry(
      JwtCirce.decode(token, key, Seq(alg)).ensure(NotAuthorized)(_.isValid(issuer, audience))
    )

  def getId(token: String): F[Either[Throwable, String]] =
    decodeJwt(token).attemptT
      .flatMap(jwt => EitherT.fromOption[F].apply[Throwable, String](jwt.subject, NotAuthorized))
      .value

}
