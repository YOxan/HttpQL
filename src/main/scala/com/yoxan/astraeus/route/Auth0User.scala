package com.yoxan.astraeus.route

case class Auth0User(
    auth0Id: String,
    lastName: String,
    firstName: String,
    middleName: Option[String]
)
