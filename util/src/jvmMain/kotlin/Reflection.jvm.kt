package quest.laxla.spock

import java.util.Objects

@JvmSynthetic
internal actual fun Any.toIdentityStringImpl(): String = Objects.toIdentityString(this)
