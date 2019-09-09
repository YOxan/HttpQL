package com.yoxan.astraeus.user

case class Auth0User[IdType](
    id: IdType,
    lastName: String,
    firstName: String,
    middleName: Option[String]
)
