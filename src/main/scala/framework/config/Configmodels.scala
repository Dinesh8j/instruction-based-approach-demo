package framework.config

case class StepConfig(
                       step:         String,
                       responseType: Option[String],          // used by: deserialize
                       packageName:  Option[String],          // used by: invoke
                       className:    Option[String],          // used by: invoke
                       method:       Option[String],          // used by: invoke
                       args:         List[String]             // used by: invoke
                     )

case class FeatureConfig(
                          name:  String,
                          steps: List[StepConfig]
                        )
