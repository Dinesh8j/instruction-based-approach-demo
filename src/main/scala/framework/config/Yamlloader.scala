package framework.config

import org.yaml.snakeyaml.Yaml
import scala.jdk.CollectionConverters._

object YamlLoader {

  def load(path: String): FeatureConfig = {
    val yaml   = new Yaml()
    val stream = getClass.getResourceAsStream(path)

    if (stream == null)
      throw new RuntimeException(s"[YamlLoader] Config not found: $path")

    val root    = yaml.load[java.util.Map[String, Any]](stream)
    val feature = root.get("feature").asInstanceOf[java.util.Map[String, Any]]

    val steps = feature.get("steps")
      .asInstanceOf[java.util.List[java.util.Map[String, Any]]]
      .asScala.toList
      .map { s =>
        val args: List[String] =
          Option(s.get("args"))
            .map(_.asInstanceOf[java.util.List[Any]].asScala.toList.map(_.toString))
            .getOrElse(List.empty)

        StepConfig(
          step               = s.get("step").toString,
          responseType       = Option(s.get("response_type")).map(_.toString),
          packageName        = Option(s.get("package")).map(_.toString),
          className          = Option(s.get("class")).map(_.toString),
          method             = Option(s.get("method")).map(_.toString),
          args               = args,
          fetchFromPersistId = Option(s.get("fetch-from-persist-id")).map(_.toString.toInt)
        )
      }

    FeatureConfig(
      name  = feature.get("name").toString,
      steps = steps
    )
  }
}
