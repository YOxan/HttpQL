package com.yoxan.astraeus.user

case class e[IdType](
    id: IdType,
    lastName: String,
    firstName: String,
    middleName: Option[String]
)
