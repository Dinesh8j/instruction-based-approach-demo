package framework

import framework.config.StepConfig
import org.slf4j.LoggerFactory

object StepExecutor {

  private val log = LoggerFactory.getLogger(getClass)

  // stepIndex passed in so PersistStore can key every output by position
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
        val fetchId = step.fetchFromPersistId.getOrElse(
          throw new RuntimeException("[StepExecutor] 'fetch-from-persist-id' required on invoke step")
        )
        val source = PersistStore.fetch(fetchId)
        log.info(s"[Step: invoke] Fetched persist-id=$fetchId  value=$source")
        val out = UtilInvoker.run(step, source)
        log.info(s"[Step: invoke] Output=$out")
        out

      case unknown =>
        throw new RuntimeException(s"[StepExecutor] Unknown step: '$unknown'")
    }

    // Always store every step output by index — no condition, no opt-in
    PersistStore.save(stepIndex, result)

    // If step has persist-id, register it as a named alias over that index
    step.persistId.foreach { id =>
      PersistStore.registerAlias(id, stepIndex)
    }

    result
  }
}
