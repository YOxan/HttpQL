package com.yoxan.astraeus.graphql

import cats.Monad
import cats.data.EitherT
import cats.effect._
import com.yoxan.astraeus.error.ServerError
import com.yoxan.astraeus.route.Query
import com.yoxan.astraeus.util._
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ ExceptionHandler, Executor, HandledException }
import sangria.marshalling.circe
import sangria.marshalling.circe._
import sangria.parser.QueryParser

import scala.concurrent.{ ExecutionContext, Future }

class GraphQLResolver[F[_]: Effect: Monad, Ctx](
    resolver: DeferredResolver[Ctx],
    schemaDefinition: SchemaDefinition[Ctx]
)(
    implicit val ec: ExecutionContext
) {

  private val customExceptionHandler = ExceptionHandler(
    onException = {
      case (m, error: ServerError) => {
        error.printStackTrace()
        HandledException(
          error.msg,
          error.errorCode
            .map(c => Map("errorCode" -> m.scalarNode(c, "Int", Set.empty)))
            .getOrElse(Map.empty)
        )
      }

      case (_, ex: Throwable) => HandledException(ex.getMessage)
    },
    onViolation = {
      case (_, v) => HandledException(v.errorMessage)
    }
  )

  private[graphql] def executeFuture(
      graphQLContext: Ctx,
      queryAst: sangria.ast.Document,
      variables: String
  ): Future[circe.CirceResultMarshaller.Node] =
    Executor
      .execute(
        schemaDefinition.schema,
        queryAst,
        graphQLContext,
        deferredResolver = resolver,
        exceptionHandler = customExceptionHandler
        //variables = variables
      )

  private[graphql] def execute(
      graphQLContext: Ctx,
      queryAst: sangria.ast.Document,
      variables: String
  ): F[circe.CirceResultMarshaller.Node] =
    executeFuture(graphQLContext, queryAst, variables)
      .toAsync[F]

  def execute(context: Ctx, query: Query): EitherT[F, Throwable, circe.CirceResultMarshaller.Node] =
    EitherT
      .fromEither[F](QueryParser.parse(query.query).toEither)
      .flatMapF(d => Sync[F].attempt(execute(context, d, query.variables)))
}
