package com.yoxan.astraeus

import io.circe.generic.auto._
import tapir._
import tapir.json.circe._

package object error {
  val errorBody = jsonBody[ErrorDTO]
    .description("Error during computation on logic")
    .example(TestError.toDTO())
}
