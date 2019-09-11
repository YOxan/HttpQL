package com.yoxan.astraeus

import cats.effect.Effect

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

package object util {
  implicit class FutureOps[T](f: Future[T]) {
    def toAsync[F[_]](implicit E: Effect[F], ec: ExecutionContext): F[T] = E.async[T] { cb =>
      f.onComplete {
        case Success(node) => cb(Right(node))
        case Failure(ex)   => cb(Left(ex))
      }
    }
  }

}
