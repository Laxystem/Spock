package quest.laxla.spock

private val currentTarget = Target.WasmJs(null) // TODO

@ExperimentalSpockApi
public actual val Target.Companion.current: Target get() = currentTarget
