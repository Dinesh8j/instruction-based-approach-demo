package framework.config

case class StepConfig(
  step:               String,
  responseType:       Option[String],   // deserialize step
  packageName:        Option[String],   // invoke step
  className:          Option[String],   // invoke step
  method:             Option[String],   // invoke step
  args:               List[String],     // invoke step
  fetchFromPersistId: Option[Int]       // fetch output of step[index] from store
)

case class FeatureConfig(
  name:  String,
  steps: List[StepConfig]
)
