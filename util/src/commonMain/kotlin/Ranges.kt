package quest.laxla.spock

/**
 * Returns the amount of [Int]s within this range.
 *
 * @since 0.0.1-alpha.4
 */
public val IntRange.size: Int get() = last - first

/**
 * Returns the amount of [Long]s within this range.
 *
 * @since 0.0.1-alpha.4
 */
public val LongRange.size: Long get() = last - first

/**
 * Returns a view of the portion of this list within this range.
 * 
 * @since 0.0.1-alpha.4
 */
public infix fun <T> IntRange.at(list: List<T>): List<T> = list.subList(first, last + 1)

/**
 * Returns a view of this list excluding everything before [indexInclusive].
 * 
 * @since 0.0.1-alpha.4
 */
public fun <T> List<T>.from(indexInclusive: Int): List<T> = subList(indexInclusive, size)

/**
 * Returns a view of this list excluding the first element.
 * 
 * @since 0.0.1-alpha.4
 */
public fun <T> List<T>.skipFirst(): List<T> = from(1)

/**
 * Returns a progression that goes over the same range with the given step.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun UIntProgression.step(step: UInt): UIntProgression = step(step.toInt())

/**
 * Returns a progression that goes over the same range with the given step.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun ULongProgression.step(step: ULong): ULongProgression = step(step.toLong())
