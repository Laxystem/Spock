package quest.laxla.spock

@Suppress("NOTHING_TO_INLINE")
public actual inline fun Int.growArraySize(minimumGrowth: Int, preferredGrowth: Int): Int =
	jdk.internal.util.ArraysSupport.newLength(this, minimumGrowth, preferredGrowth)

@PublishedApi
internal actual val SoftMaxArrayLength: Int get() = jdk.internal.util.ArraysSupport.SOFT_MAX_ARRAY_LENGTH

@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
internal actual typealias OutOfMemoryError = java.lang.OutOfMemoryError
