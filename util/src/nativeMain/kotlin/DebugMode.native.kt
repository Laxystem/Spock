package quest.laxla.spock

import kotlin.experimental.ExperimentalNativeApi

/**
 * Kotlin/Native runtime assertions.
 *
 * @since 0.0.1-alpha.4
 */
@ExperimentalNativeApi
public data object BytecodeAssertions : DebugMode.Component {
	override var isDefinitive: Boolean = true

	override val isEnabled: Boolean = try {
		assert(false)
		false
	} catch (_: AssertionError) {
		true
	}
}

@OptIn(ExperimentalNativeApi::class)
internal actual fun DebugMode.onInit() {
	register(BytecodeAssertions)
}
