package example

package example

import cats.implicits._
import com.yoxan.astraeus.route.Api
import cats.effect.ContextShift
import cats.effect.IO
import scala.concurrent.ExecutionContext
import com.yoxan.astraeus.Server
import com.yoxan.astraeus.config.AppConfig
import com.yoxan.astraeus.config.ServerConfig
import cats.effect.IOApp
import cats.effect.ExitCode

object TestServer extends IOApp {
  implicit val ec = ExecutionContext.global

  val configF = IO(AppConfig(ServerConfig("localhost", 8080)))
  val api     = Api(TestScheme, () => IO(TestCtx))

  override def run(args: List[String]): IO[ExitCode] =
    new Server(configF).start(api, None) *>
        IO.pure(ExitCode.Success)

}
