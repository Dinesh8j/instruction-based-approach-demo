package framework

import framework.config.StepConfig
import org.slf4j.LoggerFactory

object StepExecutor {

  private val log = LoggerFactory.getLogger(getClass)

  // deserializedResult carries the case class instance from the deserialize step
  def run(step: StepConfig, input: String, deserializedResult: Option[Any] = None): Any = {
    step.step match {

      case "deserialize" =>
        val responseType = step.responseType.getOrElse(
          throw new RuntimeException("[StepExecutor] 'response_type' is required on deserialize step")
        )
        log.info(s"[Step: deserialize] response_type = $responseType")
        val result = Deserializer.run(input, responseType)
        log.info(s"[Step: deserialize] Output = $result")
        result

      case "log" =>
        log.info(s"[Step: log] $input")
        input

      case "invoke" =>
        val target = s"${step.packageName.getOrElse("")}.${step.className.getOrElse("")}.${step.method.getOrElse("")}"
        log.info(s"[Step: invoke] Calling $target with args = ${step.args}")
        val resolved = deserializedResult.getOrElse(
          throw new RuntimeException("[StepExecutor] invoke step requires deserialize to run first")
        )
        val result = UtilInvoker.run(step, resolved)
        log.info(s"[Step: invoke] Output = $result")
        result

      case unknown =>
        throw new RuntimeException(s"[StepExecutor] Unknown step type: '$unknown'")
    }
  }
}