package framework.controller

import framework.FrameworkController


object Main {

  def main(args: Array[String]): Unit = {

    // This is the raw input string the framework receives
    val input =
      """
        |{
        |  "forecast_id": "FORECAST_001",
        |  "hierarchy_type": "user",
        |  "forecast_action": "compute",
        |  "query_string": {
        |    "forecast_value_query": "SELECT SUM(amount) FROM deals",
        |    "forecast_committed_query": "SELECT SUM(amount) FROM deals WHERE stage = 'committed'",
        |    "forecast_record_wise_query": "SELECT * FROM deals"
        |  },
        |  "target_config_api": "https://api.zoho.com/target/config",
        |  "target_type": "monthly",
        |  "callback_api": "https://api.zoho.com/callback",
        |  "forecast_config": {
        |    "forecast_period": "Q1_2024",
        |    "forecast_start_time": 1704067200000,
        |    "analysis_start_time": 1701388800000
        |  },
        |  "count_of_deals": false,
        |  "forecast_category": {
        |    "pipeline": "pipeline_stage",
        |    "closed": "closed_won",
        |    "omitted": "omitted_stage",
        |    "best_case": "best_case_stage",
        |    "committed": "committed_stage"
        |  },
        |  "zia_score": {
        |    "is_enabled": true,
        |    "zia_score_field_name": "zia_prediction_score"
        |  },
        |  "split_type": "equal",
        |  "fiscal_week": 1,
        |  "run_deal_component": true
        |}
      """.stripMargin

    // Framework takes it from here — reads config.yaml, deserializes, logs
    FrameworkController.process(input, "/Pipeline.yaml")
  }
}