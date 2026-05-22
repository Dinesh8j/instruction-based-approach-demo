package framework

import framework.config.YamlLoader
import org.slf4j.LoggerFactory

object FrameworkController {

  private val log = LoggerFactory.getLogger(getClass)

  def process(input: String, configPath: String): Any = {

    log.info(s"[Controller] Input   : $input")
    log.info(s"[Controller] Config  : $configPath")

    PersistStore.clear()

    val config = YamlLoader.load(configPath)
    log.info(s"[Controller] Feature : ${config.name}")
    log.info(s"[Controller] Steps   : ${config.steps.map(_.step).mkString(" -> ")}")

    var result: Any = input

    config.steps.zipWithIndex.foreach { case (stepConfig, index) =>
      log.info(s"[Controller] -- step[$index]: ${stepConfig.step}")
      result = StepExecutor.run(stepConfig, input, index)
    }

    log.info(s"[Controller] Done. Final output: $result")
    result
  }
}
