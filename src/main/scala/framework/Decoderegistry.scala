package framework

import play.api.libs.json.Reads
import com.zoho.crm.feature.forecast.integration.v4.schemas.ForecastInputRequest

// Developer adds one line here when they add a new model.
// Just point to the FORMAT they already wrote — no new code needed.
object DecoderRegistry {

  val all: Map[String, Reads[_]] = Map(
    "com.zoho.crm.feature.forecast.integration.v4.schemas.ForecastInputRequest"
      -> ForecastInputRequest.FORMAT
  )

  def find(responseType: String): Reads[_] =
    all.getOrElse(responseType,
      throw new RuntimeException(
        s"[DecoderRegistry] No Reads registered for '$responseType'. " +
          s"Add it to DecoderRegistry.all"
      )
    )
}