package quest.laxla.spock

@ExperimentalSpockApi
public actual val Target.Companion.current: Target
	get() = Target.WasmWasi()