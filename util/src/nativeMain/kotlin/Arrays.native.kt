package quest.laxla.spock

import kotlin.OutOfMemoryError

@Suppress("NOTHING_TO_INLINE")
public actual inline fun Int.growArraySize(minimumGrowth: Int, preferredGrowth: Int): Int =
	growArraySizeImpl(minimumGrowth, preferredGrowth)

@PublishedApi
internal actual val SoftMaxArrayLength: Int get() = DefaultSoftMaxArrayLength

@Suppress("ACTUAL_ANNOTATIONS_NOT_MATCH_EXPECT")
internal actual typealias OutOfMemoryError = OutOfMemoryError
