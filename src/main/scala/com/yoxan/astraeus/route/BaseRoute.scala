package com.yoxan.astraeus.route

import cats.arrow.FunctionK
import cats.effect.Async
import cats.implicits._
import com.yoxan.astraeus.error.{ ErrorDTO, ServerError }

class BaseRoute[F[_]: Async] {
  type ErrRes[T] = F[Either[ErrorDTO, T]]

  val catchError = new FunctionK[F, ErrRes] {
    override def apply[A](fa: F[A]): ErrRes[A] =
      Async[F].attempt(fa).map(_.leftMap(ex => ServerError.toError(ex).toDTO()))
  }
}
