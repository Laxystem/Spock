package quest.laxla.spock

private val currentTarget = KTarget.WasmJs(null) // TODO

@ExperimentalSpockApi
public actual val KTarget.Companion.current: KTarget get() = currentTarget
