package com.yoxan.astraeus.error

case class AdditionalField(errorCode: Int)
case class MessagedError(message: String, extensions: Option[AdditionalField])
case class ErrorDTO(errors: Seq[MessagedError])

case class ServerError(msg: String, errorCode: Option[Int] = None) extends Exception(msg)

object TestError extends ServerError("Test error", Some(0))
object NotAuthorized extends ServerError("User not authorized", Some(101))
object ProfileNotFound extends ServerError("Profile not found", Some(201))

object ServerError {

  implicit class ServerErrorOps(serverError: ServerError) {
    def toDTO(): ErrorDTO =
      ErrorDTO(Seq(MessagedError(serverError.msg, serverError.errorCode.map(AdditionalField))))
  }

  def toError(ex: Throwable): ServerError = ex match {
    case s: ServerError => s
    case _              => ServerError(ex.getMessage, None)
  }
}
