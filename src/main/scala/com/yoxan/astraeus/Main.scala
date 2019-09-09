package com.yoxan.astraeus

import cats.effect.{ ExitCode, IO, IOApp }
import cats.implicits._
import com.contact4u.config.ConfigLoader
import com.contact4u.db.MongoDB
import com.contact4u.route.ApiV1
import com.google.inject.Guice
import com.yoxan.astraeus.config.ConfigLoader
import com.yoxan.astraeus.route.ApiV1
import net.codingwell.scalaguice.InjectorExtensions._

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    val mongoCfg = ConfigLoader.loadMongoConfig[IO]
    val httpCfg  = ConfigLoader.loadServerConfig[IO]

    //TODO: move to basic container
    new MongoDB(mongoCfg).mkDatabase.use { db =>
      val injector =
        Guice.createInjector(new WithMongoModule(db), new WithIOModule, new WithGraphQLModule)
      val apiRoutes = injector.instance[ApiV1[IO]]
      val server    = new Server[IO](httpCfg)

      server
        .start(apiRoutes)
        .as(ExitCode.Success)
    }

  }
}
