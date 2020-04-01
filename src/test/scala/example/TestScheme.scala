package example

import com.yoxan.astraeus.graphql.SchemaDefinition
import sangria.schema._

object TestScheme extends SchemaDefinition[TestCtx.type] {

  case class TestObject(name: String)

  val TestObjectType = ObjectType(
    "testObject",
    "Some test object",
    fields[TestCtx.type, TestObject](
      Field(
        "name",
        StringType,
        resolve = _.value.name
      )
    )
  )

  val QueryObject = ObjectType(
    "query",
    fields[TestCtx.type, Any](
      Field(
        "testObject",
        TestObjectType,
        resolve = ctx => TestObject("testName")
      )
    )
  )

  override val schema: Schema[TestCtx.type, Any] = Schema(QueryObject)
}
