package quest.laxla.spock

@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
internal actual fun Any.toIdentityStringImpl(): String = this::class.fallbackName + '@' + "<unknown>"