package quest.laxla.spock

private val currentTarget = Target.WasmJs(null) // TODO
public actual val Target.Companion.current: Target get() = currentTarget
