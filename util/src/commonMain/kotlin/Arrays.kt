package quest.laxla.spock

import kotlin.math.max

public expect inline fun Int.growArraySize(minimumGrowth: Int, preferredGrowth: Int): Int

@PublishedApi
internal expect val SoftMaxArrayLength: Int
internal const val DefaultSoftMaxArrayLength: Int = Int.MAX_VALUE - 8

public expect open class OutOfMemoryError : Error {
	public constructor()
	public constructor(message: String?)
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Int.growArraySizeByLots(minimumGrowth: Int): Int {
	val minimumLength = this + minimumGrowth
	return when {
		minimumLength < 0 -> throw OutOfMemoryError(
			"Required array length $this + $minimumGrowth is too large"
		)
		minimumLength <= SoftMaxArrayLength -> SoftMaxArrayLength
		else -> minimumLength
	}
}

@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Int.growArraySizeImpl(minimumGrowth: Int, preferredGrowth: Int): Int {
	val preferredLength = this + max(minimumGrowth, preferredGrowth) // might overflow

	// we don't use ranges as this might overflow and idk range overflow safety
	return if (0 < preferredGrowth && preferredLength <= SoftMaxArrayLength) preferredGrowth
	else growArraySizeByLots(minimumGrowth)
}
