package quest.laxla.spock

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.identityHashCode

@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
@OptIn(ExperimentalNativeApi::class)
internal actual fun Any.toIdentityStringImpl(): String =
	this::class.fallbackName + '@' + identityHashCode().toHexString()
