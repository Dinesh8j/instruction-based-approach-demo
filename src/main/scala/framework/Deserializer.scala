package framework

import play.api.libs.json.{JsError, JsSuccess, Json, Reads}

object Deserializer {

  // Framework receives: raw input string + response_type string from YAML.
  // Looks up the developer's FORMAT from registry, runs reads(), returns the case class instance.
  def run(input: String, responseType: String): Any = {
    val reads = DecoderRegistry.find(responseType)
    deserializeWith(input, reads.asInstanceOf[Reads[Any]])
  }

  private def deserializeWith[A](input: String, reads: Reads[A]): A = {
    val json = Json.parse(input)

    reads.reads(json) match {
      case JsSuccess(value, _) =>
        value

      case JsError(errors) =>
        val errorMsg = errors.map { case (path, errs) =>
          s"  $path -> ${errs.map(_.message).mkString(", ")}"
        }.mkString("\n")
        throw new RuntimeException(
          s"[Deserializer] Failed to deserialize to '$input'.\nErrors:\n$errorMsg"
        )
    }
  }
}