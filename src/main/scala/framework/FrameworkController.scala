package framework

import framework.config.YamlLoader
import org.slf4j.LoggerFactory

object FrameworkController {

  private val log = LoggerFactory.getLogger(getClass)

  def process(input: String, configPath: String): Any = {

    log.info(s"[Controller] Input    : $input")
    log.info(s"[Controller] Config   : $configPath")

    val config = YamlLoader.load(configPath)

    log.info(s"[Controller] Feature  : ${config.name}")
    log.info(s"[Controller] Steps    : ${config.steps.map(_.step).mkString(" -> ")}")

    var result: Any             = input
    var deserialized: Option[Any] = None   // carries case class instance for invoke step

    config.steps.foreach { stepConfig =>
      log.info(s"[Controller] -- Running step: ${stepConfig.step}")

      result = StepExecutor.run(stepConfig, input, deserialized)

      // Once deserialize runs, hold the result for subsequent invoke steps
      if (stepConfig.step == "deserialize") deserialized = Some(result)
    }

    log.info(s"[Controller] Done. Final output: $result")
    result
  }
}