package quest.laxla.spock

import kotlin.math.max

/**
 * Provides the optimized new array size for an array of [this] size that needs to grow at least [minimumGrowth].
 *
 * @since 0.0.1-alpha.4
 */
public expect inline fun Int.growArraySize(minimumGrowth: Int, preferredGrowth: Int): Int

/**
 * The maximum length of an array as allowed by the target platform.
 *
 * Some targets will throw an [OutOfMemoryError] for large allocations even when enough heap space is available,
 * due to implementation details. Thus, this value is chosen conservatively, so that it is smaller than all
 * implementations of the target platform.
 *
 * @since 0.0.1-alpha.4
 */
@PublishedApi
internal expect val SoftMaxArrayLength: Int

// TODO: remove this default, localize per target.
internal const val DefaultSoftMaxArrayLength: Int = Int.MAX_VALUE - 8

@PublishedApi
internal expect open class OutOfMemoryError : Error {
	constructor()
	constructor(message: String?)
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

// TODO: remove this default, localize
@PublishedApi
@Suppress("NOTHING_TO_INLINE")
internal inline fun Int.growArraySizeImpl(minimumGrowth: Int, preferredGrowth: Int): Int {
	val preferredLength = this + max(minimumGrowth, preferredGrowth) // might overflow

	// we don't use ranges as this might overflow and idk range overflow safety
	return if (0 < preferredGrowth && preferredLength <= SoftMaxArrayLength) preferredGrowth
	else growArraySizeByLots(minimumGrowth)
}
