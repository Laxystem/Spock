package quest.laxla.spock

import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_AUTO
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_OFF
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON

/**
 * Java 1.4 assertions.
 *
 * @since 0.0.1-alpha.4
 */
public data object BytecodeAssertions : DebugMode.Component {
	override var isDefinitive: Boolean = true

	override val isEnabled: Boolean = javaClass.desiredAssertionStatus()
}

/**
 * KotlinX Coroutines' debug mode.
 *
 * @since 0.0.1-alpha.4
 */
public data object KotlinXCoroutinesDebugMode : DebugMode.Component {
	override val isEnabled: Boolean = when (val value = System.getProperty(DEBUG_PROPERTY_NAME)) {
		DEBUG_PROPERTY_VALUE_AUTO, null -> BytecodeAssertions.isEnabled
		DEBUG_PROPERTY_VALUE_ON, "" -> true
		DEBUG_PROPERTY_VALUE_OFF -> false
		else -> error("System property '$DEBUG_PROPERTY_NAME' has unrecognized value '$value'")
	}
}

internal actual fun DebugMode.onInit() {
	register(BytecodeAssertions)
	register(KotlinXCoroutinesDebugMode)
}
