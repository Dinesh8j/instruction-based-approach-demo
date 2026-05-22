package framework

import framework.config.StepConfig
import org.slf4j.LoggerFactory

object StepExecutor {

  private val log = LoggerFactory.getLogger(getClass)

  def run(step: StepConfig, input: String, stepIndex: Int): Any = {

    val result = step.step match {

      case "deserialize" =>
        val responseType = step.responseType.getOrElse(
          throw new RuntimeException("[StepExecutor] 'response_type' required on deserialize step")
        )
        log.info(s"[Step: deserialize] response_type=$responseType")
        val out = Deserializer.run(input, responseType)
        log.info(s"[Step: deserialize] Output=$out")
        out

      case "log" =>
        log.info(s"[Step: log] $input")
        input

      case "invoke" =>
        val fetchIndex = step.fetchFromPersistId.getOrElse(
          throw new RuntimeException("[StepExecutor] 'fetch-from-persist-id' required on invoke step")
        )
        val source = PersistStore.fetch(fetchIndex)
        log.info(s"[Step: invoke] Fetched step[$fetchIndex]  →  $source")
        val out = UtilInvoker.run(step, source)
        log.info(s"[Step: invoke] Output=$out")
        out

      case unknown =>
        throw new RuntimeException(s"[StepExecutor] Unknown step: '$unknown'")
    }

    // Always save every step output — no condition, no opt-in
    PersistStore.save(stepIndex, result)

    result
  }
}
