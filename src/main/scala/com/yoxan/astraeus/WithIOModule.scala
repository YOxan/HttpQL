package com.yoxan.astraeus

import cats.{ Applicative, Functor }
import cats.effect._
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

import scala.concurrent.ExecutionContext

class WithIOModule extends AbstractModule with ScalaModule {
  val ec = ExecutionContext.global

  override def configure(): Unit = {
    bind[ExecutionContext].toInstance(ec)
    bind[ContextShift[IO]].toInstance(IO.contextShift(ec))
    //FIXME: Fix it shouldn't be here
    bind[Async[IO]].toInstance(implicitly[Async[IO]])
    //FIXME: Fix it shouldn't be here
    bind[Sync[IO]].toInstance(implicitly[Sync[IO]])
    //FIXME: Fix it shouldn't be here
    bind[Functor[IO]].toInstance(implicitly[Functor[IO]])
    //FIXME: Fix it shouldn't be here
    bind[Effect[IO]].toInstance(implicitly[Effect[IO]])
    bind[Applicative[IO]].toInstance(implicitly[Applicative[IO]])
  }
}
