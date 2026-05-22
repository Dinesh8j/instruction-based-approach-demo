package framework

import framework.config.StepConfig

object UtilInvoker {

  // Developer's class is a regular class (not object), so we instantiate it.
  // Then call the method with the args extracted from the deserialized result.
  def run(step: StepConfig, deserializedInput: Any): Any = {
    val packageName = step.packageName.getOrElse(throw new RuntimeException("[UtilInvoker] 'package' is required on invoke step"))
    val className   = step.className.getOrElse(throw new RuntimeException("[UtilInvoker] 'class' is required on invoke step"))
    val methodName  = step.method.getOrElse(throw new RuntimeException("[UtilInvoker] 'method' is required on invoke step"))

    val fullClassName = s"$packageName.$className"

    // Instantiate the class (regular class, not companion object)
    val clazz    = Class.forName(fullClassName)
    val instance = clazz.getDeclaredConstructor().newInstance()

    // Extract arg values from the deserialized case class using field names from YAML
    val argValues: Array[Object] = step.args.map { fieldName =>
      extractField(deserializedInput, fieldName)
    }.toArray

    // Find method matching name and arg count
    val method = clazz.getMethods
      .find(m => m.getName == methodName && m.getParameterCount == argValues.length)
      .getOrElse(throw new RuntimeException(
        s"[UtilInvoker] Method '$methodName' with ${argValues.length} args not found in $fullClassName"
      ))

    val result = method.invoke(instance, argValues: _*)
    result
  }

  // Reads a field value from the deserialized case class by field name
  private def extractField(obj: Any, fieldName: String): Object = {
    val field = obj.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.get(obj)
  }
}