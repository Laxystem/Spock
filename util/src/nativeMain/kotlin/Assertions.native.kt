package quest.laxla.spock

import kotlin.experimental.ExperimentalNativeApi
import kotlin.assert as assertIntrinsic

@OptIn(ExperimentalNativeApi::class)
public actual inline fun assert(value: Boolean, lazyMessage: () -> String) {
	assertIntrinsic(value) {
		runtimeAssertSuccessful(lazyMessage)
	}
}

@OptIn(ExperimentalNativeApi::class)
public actual inline fun assert(predicate: () -> Boolean, lazyMessage: () -> String) {
	if (BytecodeAssertions.isEnabled) assert(predicate(), lazyMessage)
}
