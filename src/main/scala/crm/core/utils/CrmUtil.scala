package crm.core.utils

class CrmUtil{

  def helper(forecast_id : String,hierarchy_type:String):InsightsResponse={
    InsightsResponse(res="tested")
  }

  case class InsightsResponse(
                               res:String
                             )

}
