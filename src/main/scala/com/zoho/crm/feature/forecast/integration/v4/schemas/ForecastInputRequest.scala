package com.zoho.crm.feature.forecast.integration.v4.schemas

import play.api.libs.json.{Format, JsResult, JsSuccess, JsValue, Json}


case class QueryString(forecast_value_query: String,
                       forecast_committed_query: String,
                       forecast_record_wise_query: String
                      )

object QueryString {

  implicit val FORMAT: Format[QueryString] = new Format[QueryString] {

    override def reads(json: JsValue): JsResult[QueryString] = {
      val x = QueryString(
        forecast_value_query = (json \ "forecast_value_query").as[String],
        forecast_record_wise_query = (json \ "forecast_record_wise_query").as[String],
        forecast_committed_query = (json \ "forecast_committed_query").as[String])
      JsSuccess(x)
    }

    override def writes(input: QueryString): JsValue = {
      Json.obj("forecast_value_query" -> input.forecast_value_query,
        "forecast_record_wise_query" -> input.forecast_record_wise_query,
        "forecast_committed_query" -> input.forecast_committed_query
      )
    }
  }
}


case class ForecastConfig(forecast_period: String,
                          forecast_start_time: Long,
                          analysis_start_time: Long
                         )

object ForecastConfig {

  implicit val FORMAT: Format[ForecastConfig] = new Format[ForecastConfig] {

    override def reads(json: JsValue): JsResult[ForecastConfig] = {
      val x = ForecastConfig(
        forecast_period = (json \ "forecast_period").as[String],
        forecast_start_time = (json \ "forecast_start_time").as[Long],
        analysis_start_time = (json \ "analysis_start_time").as[Long])
      JsSuccess(x)
    }

    override def writes(input: ForecastConfig): JsValue = {
      Json.obj("forecast_period" -> input.forecast_period,
        "forecast_start_time" -> input.forecast_start_time,
        "analysis_start_time" -> input.analysis_start_time
      )
    }
  }
}


case class ForecastCategory(pipeline: String,
                            closed: String,
                            omitted: String,
                            best_case: String,
                            committed: String)

object ForecastCategory {

  implicit val FORMAT: Format[ForecastCategory] = new Format[ForecastCategory] {

    override def reads(json: JsValue): JsResult[ForecastCategory] = {
      val x = ForecastCategory(
        pipeline = (json \ "pipeline").as[String],
        closed = (json \ "closed").as[String],
        omitted = (json \ "omitted").as[String],
        best_case = (json \ "best_case").as[String],
        committed = (json \ "committed").as[String])
      JsSuccess(x)
    }

    override def writes(input: ForecastCategory): JsValue = {
      Json.obj("pipeline" -> input.pipeline,
        "closed" -> input.closed,
        "omitted" -> input.omitted,
        "best_case" -> input.best_case,
        "committed" -> input.committed
      )
    }
  }
}


case class ZiaScore(is_enabled: Boolean,
                    zia_score_field_name: Option[String])

object ZiaScore {

  implicit val FORMAT: Format[ZiaScore] = new Format[ZiaScore] {

    override def reads(json: JsValue): JsResult[ZiaScore] = {
      val x = ZiaScore(
        is_enabled = (json \ "is_enabled").as[Boolean],
        zia_score_field_name = (json \ "zia_score_field_name").asOpt[String])
      JsSuccess(x)
    }

    override def writes(input: ZiaScore): JsValue = {
      Json.obj("is_enabled" -> input.is_enabled,
        "zia_score_field_name" -> input.zia_score_field_name
      )
    }
  }
}


case class ForecastInputRequest(forecast_id: String,
                                hierarchy_type: String,
                                forecast_action: String,
                                query_string: QueryString,
                                target_config_api: String,
                                target_type: String,
                                callback_api: String,
                                forecast_config: ForecastConfig,
                                count_of_deals: Boolean,
                                forecast_category: ForecastCategory,
                                zia_score: Option[ZiaScore],
                                split_type: String,
                                fiscal_week: Int,
                                run_deal_component: Boolean
                               )

object ForecastInputRequest {

  implicit val FORMAT: Format[ForecastInputRequest] = new Format[ForecastInputRequest] {

    override def reads(json: JsValue): JsResult[ForecastInputRequest] = {
      val x = ForecastInputRequest(
        forecast_id = (json \ "forecast_id").as[String],
        hierarchy_type = (json \ "hierarchy_type").as[String],
        forecast_action = (json \ "forecast_action").as[String],
        query_string = (json \ "query_string").as[QueryString],
        target_config_api = (json \ "target_config_api").as[String],
        target_type = (json \ "target_type").as[String],
        callback_api = (json \ "callback_api").as[String],
        forecast_config = (json \ "forecast_config").as[ForecastConfig],
        count_of_deals = (json \ "count_of_deals").as[Boolean],
        forecast_category = (json \ "forecast_category").as[ForecastCategory],
        zia_score = (json \ "zia_score").asOpt[ZiaScore],
        split_type = (json \ "split_type").as[String],
        fiscal_week = (json \ "fiscal_week").as[Int],
        run_deal_component = (json \ "run_deal_component").as[Boolean])
      JsSuccess(x)
    }

    override def writes(input: ForecastInputRequest): JsValue = {
      Json.obj("forecast_id" -> input.forecast_id,
        "hierarchy_type" -> input.hierarchy_type,
        "forecast_action" -> input.forecast_action,
        "query_string" -> input.query_string,
        "target_config_api" -> input.target_config_api,
        "target_type" -> input.target_type,
        "callback_api" -> input.callback_api,
        "forecast_config" -> input.forecast_config,
        "count_of_deals" -> input.count_of_deals,
        "forecast_category" -> input.forecast_category,
        "zia_score" -> input.zia_score,
        "fiscal_week" -> input.fiscal_week,
        "split_type" -> input.split_type,
        "run_deal_component" -> input.run_deal_component
      )
    }
  }
}