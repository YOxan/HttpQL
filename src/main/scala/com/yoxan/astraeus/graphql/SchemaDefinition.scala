package com.yoxan.astraeus.graphql

import sangria.renderer.SchemaRenderer
import sangria.schema._

trait SchemaDefinition[Ctx] {
  val schema: Schema[Ctx, Any]
  lazy val render = SchemaRenderer.renderSchema(schema)
}
