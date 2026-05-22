package framework

// Every step output is stored automatically by step index.
// No persist-id in YAML. Developer uses fetch-from-persist-id: <index>
// to reference the output of any earlier step by its position.

object PersistStore {

  private var store: Map[Int, Any] = Map.empty

  // Called after every step automatically — no opt-in needed
  def save(stepIndex: Int, value: Any): Unit = {
    store = store + (stepIndex -> value)
    println(s"[PersistStore] step[$stepIndex] saved  →  $value")
  }

  // Fetch output of any earlier step by its index
  def fetch(stepIndex: Int): Any =
    store.getOrElse(stepIndex,
      throw new RuntimeException(
        s"[PersistStore] No output found for step[$stepIndex]. " +
        s"Available indices: ${store.keys.toList.sorted.mkString(", ")}"
      )
    )

  def clear(): Unit = store = Map.empty
}
