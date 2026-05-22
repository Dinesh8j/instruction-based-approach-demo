package framework

// Every step output is stored automatically by step index (0, 1, 2...).
// persist-id is just an alias — a developer-friendly name for a step's output.
// fetch-from-persist-id resolves via the alias map to get the actual value.

object PersistStore {

  private var store:   Map[Int, Any] = Map.empty   // key = step index
  private var aliases: Map[Int, Int] = Map.empty   // persist-id -> step index

  // Called after every step — always stores by step index
  def save(stepIndex: Int, value: Any): Unit = {
    store = store + (stepIndex -> value)
    println(s"[PersistStore] step[$stepIndex] output saved  value=$value")
  }

  // Called when YAML has persist-id — registers an alias for that step index
  def registerAlias(persistId: Int, stepIndex: Int): Unit = {
    aliases = aliases + (persistId -> stepIndex)
    println(s"[PersistStore] persist-id=$persistId aliased to step[$stepIndex]")
  }

  // Fetch by persist-id — resolves alias to step index, then gets value
  def fetch(persistId: Int): Any = {
    val stepIndex = aliases.getOrElse(persistId,
      throw new RuntimeException(
        s"[PersistStore] No persist-id=$persistId registered. " +
        s"Registered ids: ${aliases.keys.mkString(", ")}"
      )
    )
    store.getOrElse(stepIndex,
      throw new RuntimeException(
        s"[PersistStore] step[$stepIndex] has no output yet."
      )
    )
  }

  // Fetch directly by step index — useful for debugging or chaining
  def fetchByIndex(stepIndex: Int): Any =
    store.getOrElse(stepIndex,
      throw new RuntimeException(s"[PersistStore] step[$stepIndex] not found.")
    )

  def clear(): Unit = {
    store   = Map.empty
    aliases = Map.empty
  }
}
