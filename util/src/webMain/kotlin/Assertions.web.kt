package quest.laxla.spock

public actual inline fun assert(predicate: () -> Boolean, lazyMessage: () -> String) {
	if (DebugMode.isDebug && !predicate()) throw AssertionError(runtimeAssertSuccessful(lazyMessage))
}

public actual inline fun assert(value: Boolean, lazyMessage: () -> String) {
	assert(predicate = { value }, lazyMessage)
}
