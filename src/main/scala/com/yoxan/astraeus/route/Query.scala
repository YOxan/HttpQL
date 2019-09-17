package com.yoxan.astraeus.route

import io.circe.Json

//TODO: Check variables and operationName
case class Query(query: String, variables: Json, operationName: Option[String])

object Query {
  /*implicit val varDecoder: Decoder[Map[String, String]] =
    Decoder[Json].map(_.asObject.get.toMap.mapValues(_.asString.get))
  implicit val varEncoder: Encoder[Map[String, String]] =
    Encoder[Json].contramap(m => Json.obj(m.mapValues(Json.fromString).toSeq: _*))*/
}
