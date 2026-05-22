# Forecast Framework — Code Walkthrough

## What This Is

A YAML-driven Scala framework where the developer only writes case classes and business logic.
The framework reads a config file and handles deserialization, logging, and utility invocation automatically.

---

## The Core Idea

Most frameworks make the developer write wiring code — registering handlers, writing dispatchers, hooking into pipelines.
This framework inverts that. The developer writes **only**:

- A case class with Play JSON `reads` / `writes`
- A utility method with business logic
- One line in `DecoderRegistry`

Everything else — parsing the input, deserializing to the right type, calling the right method, logging — is driven by the YAML config.

---

## File Structure

```
src/main/
├── resources/
│   └── config.yaml                        ← developer configures this only
├── scala/
│   ├── framework/
│   │   ├── FrameworkController.scala      ← entry point, orchestrates everything
│   │   ├── Deserializer.scala             ← calls Play JSON reads() on raw input
│   │   ├── DecoderRegistry.scala          ← maps response_type string → Reads[_]
│   │   ├── UtilInvoker.scala              ← loads util class via reflection, calls method
│   │   ├── StepExecutor.scala             ← routes each step type
│   │   └── config/
│   │       ├── ConfigModels.scala         ← StepConfig, FeatureConfig case classes
│   │       └── YamlLoader.scala           ← parses YAML → FeatureConfig
│   ├── com/zoho/crm/.../schemas/
│   │   └── ForecastInputRequest.scala     ← developer's case class + FORMAT
│   └── crm/core/utils/
│       └── CrmUtil.scala                  ← developer's utility method
```

---

## Flow — Step by Step

```
Raw JSON String (input)
        │
        ▼
FrameworkController.process(input, "/config.yaml")
        │
        ├─ 1. YamlLoader.load("/config.yaml")
        │       └─ reads SnakeYAML → builds FeatureConfig(name, List[StepConfig])
        │
        ├─ 2. StepExecutor.run(step=deserialize, input)
        │       └─ DecoderRegistry.find("...ForecastInputRequest")
        │               └─ returns ForecastInputRequest.FORMAT   (developer wrote this)
        │       └─ Deserializer.run(input, FORMAT)
        │               └─ Json.parse(input) → FORMAT.reads(json) → ForecastInputRequest(...)
        │
        ├─ 3. StepExecutor.run(step=log, input)
        │       └─ log.info(input)   — raw string logged to stdout
        │
        └─ 4. StepExecutor.run(step=invoke, input, deserialized)
                └─ UtilInvoker.run(step, ForecastInputRequest)
                        └─ Class.forName("crm.core.utils.CrmUtil")  — reflection
                        └─ extractField(forecastInputRequest, "forecast_id")
                        └─ extractField(forecastInputRequest, "hierarchy_type")
                        └─ crmUtil.helper(forecast_id, hierarchy_type)
                        └─ returns InsightsResponse(res="tested")
```

---

## The YAML Config

```yaml
feature:
  name: forecast_pipeline

  steps:
    - step: deserialize
      response_type: com.zoho.crm.feature.forecast.integration.v4.schemas.ForecastInputRequest

    - step: log

    - step: invoke
      package: crm.core.utils
      class: CrmUtil
      method: helper
      args:
        - forecast_id
        - hierarchy_type
```

Three things the developer declares:
- `response_type` — fully qualified class name to deserialize into
- `class` + `method` — which util to invoke
- `args` — field names to extract from the deserialized case class and pass as method arguments

---

## What the Developer Writes

### 1. Case class + Play JSON FORMAT

```scala
case class ForecastInputRequest(
  forecast_id:        String,
  hierarchy_type:     String,
  forecast_action:    String,
  query_string:       QueryString,
  forecast_config:    ForecastConfig,
  forecast_category:  ForecastCategory,
  zia_score:          Option[ZiaScore],
  // ... more fields
)

object ForecastInputRequest {
  implicit val FORMAT: Format[ForecastInputRequest] = new Format[ForecastInputRequest] {
    override def reads(json: JsValue): JsResult[ForecastInputRequest] = { ... }
    override def writes(input: ForecastInputRequest): JsValue = { ... }
  }
}
```

The developer writes `reads` and `writes` exactly as they always would for Play JSON.
No new interface to implement. No framework trait to extend. Just their normal FORMAT.

### 2. Util method

```scala
class CrmUtil {
  def helper(forecast_id: String, hierarchy_type: String): InsightsResponse = {
    InsightsResponse(res = "tested")
  }
  case class InsightsResponse(res: String)
}
```

Plain class, plain method. No annotation, no trait, no registration. Just business logic.

### 3. One line in DecoderRegistry

```scala
object DecoderRegistry {
  val all: Map[String, Reads[_]] = Map(
    "com.zoho.crm.feature.forecast.integration.v4.schemas.ForecastInputRequest"
      -> ForecastInputRequest.FORMAT
  )
}
```

This is the only framework file the developer touches — and it is one line.

---

## Key Framework Files Explained

### `YamlLoader.scala`
Uses SnakeYAML to parse `config.yaml` into `FeatureConfig`.
Extracts step type, response_type, package, class, method, and args from each step block.
Returns `List[StepConfig]` — a plain Scala case class list, no magic.

### `DecoderRegistry.scala`
A `Map[String, Reads[_]]` — maps the `response_type` string from YAML to the developer's Play JSON `Reads`.
`Deserializer` calls `find(responseType)` to get the right decoder at runtime.

### `Deserializer.scala`
Calls `Json.parse(input)` then `reads.reads(json)`.
Handles `JsSuccess` and `JsError` — throws a `RuntimeException` with field-level error details on failure.
Returns `Any` — the typed case class instance passed to subsequent steps.

### `StepExecutor.scala`
Pattern matches on `step.step`:
- `"deserialize"` → calls `Deserializer.run()`
- `"log"` → calls `log.info(input)`
- `"invoke"` → calls `UtilInvoker.run()`
Adding a new step type = adding one `case` here.

### `UtilInvoker.scala`
Uses `Class.forName(fullClassName)` to load the developer's class at runtime.
Calls `getDeclaredConstructor().newInstance()` to instantiate it.
Uses `getDeclaredField(fieldName).get(deserializedObj)` to extract arg values by name from the case class.
Finds the matching method by name and arg count via `clazz.getMethods`.
Invokes it with `method.invoke(instance, args)`.

### `FrameworkController.scala`
The single entry point. Calls `YamlLoader`, iterates `steps`, calls `StepExecutor` for each.
Maintains `deserialized: Option[Any]` — set after the `deserialize` step so `UtilInvoker`
can access the typed case class in subsequent steps.

---

## Adding a New Feature — Developer Checklist

1. Write the case class + FORMAT in the schemas package
2. Add one line to `DecoderRegistry.all`
3. Write the utility method in a plain class
4. Add a block to `config.yaml`

**Zero framework files change** beyond the one-line registry entry.

---
