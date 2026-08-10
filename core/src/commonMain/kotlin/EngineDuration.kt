@file:Suppress("DEPRECATION")

package quest.laxla.spock.core

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Represents the amount of time one instant of time is away from another instant in-"game" (i.e. [Scheduler]).
 *
 * A negative duration is possible in a situation when the second instant is earlier than the first one.
 *
 * The type can store duration values up to ±146 years with nanosecond precision,
 * and up to ±146 million years with millisecond precision.
 * If a duration-returning operation provided by Spock engine produces a duration value that doesn't fit into the above range,
 * the returned `GameDuration` is infinite.
 *
 * An infinite duration value [Duration.INFINITE] can be used to represent infinite timeouts.
 *
 * To construct a duration use either the extension function [toDuration],
 * or the extension properties [hours], [minutes], [seconds], and so on,
 * available on [Int], [Long], and [Double] numeric types.
 *
 * To get the value of this duration expressed in a particular [duration units][DurationUnit]
 * use the functions [toInt], [toLong], and [toDouble]
 * or the properties [inWholeGameSeconds], [inWholeGameMilliseconds], [inWholeGameMicroseconds], and [inWholeGameNanoseconds].
 *
 * @since 0.0.1-alpha.4
 * @see Duration
 */
@JvmInline
@Deprecated("Will be replaced with Timeline-local duration")
public value class EngineDuration internal constructor(private val impl: Duration) : Comparable<EngineDuration> {
	public operator fun unaryMinus(): EngineDuration = EngineDuration(-impl)
	public operator fun plus(other: EngineDuration): EngineDuration = EngineDuration(impl + other.impl)
	public operator fun minus(other: EngineDuration): EngineDuration = EngineDuration(impl - other.impl)
	public operator fun times(scale: Int): EngineDuration = EngineDuration(impl * scale)
	public operator fun times(scale: Double): EngineDuration = EngineDuration(impl * scale)
	public operator fun div(scale: Int): EngineDuration = EngineDuration(impl / scale)
	public operator fun div(scale: Double): EngineDuration = EngineDuration(impl / scale)
	public operator fun div(other: EngineDuration): Double = impl / other.impl

	public fun isNegative(): Boolean = impl.isNegative()
	public fun isPositive(): Boolean = impl.isPositive()
	public fun isInfinite(): Boolean = impl.isInfinite()
	public fun isFinite(): Boolean = !impl.isFinite()

	public val absoluteValue: EngineDuration get() = EngineDuration(impl.absoluteValue)

	override fun compareTo(other: EngineDuration): Int = impl.compareTo(other.impl)

	/**
	 * The value of this duration expressed as a [Long] number of seconds.
	 *
	 * An infinite duration value is converted either to [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val inWholeGameSeconds: Long
		get() = impl.inWholeSeconds

	/**
	 * The value of this duration expressed as a [Long] number of milliseconds.
	 *
	 * An infinite duration value is converted either to [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val inWholeGameMilliseconds: Long
		get() = impl.inWholeMilliseconds

	/**
	 * The value of this duration expressed as a [Long] number of microseconds.
	 *
	 * If the result doesn't fit in the range of [Long] type, it is coerced into that range:
	 * - [Long.MIN_VALUE] is returned if it's less than `Long.MIN_VALUE`,
	 * - [Long.MAX_VALUE] is returned if it's greater than `Long.MAX_VALUE`.
	 *
	 * An infinite duration value is converted either to [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val inWholeGameMicroseconds: Long
		get() = impl.inWholeMicroseconds

	/**
	 * The value of this duration expressed as a [Long] number of nanoseconds.
	 *
	 * If the result doesn't fit in the range of [Long] type, it is coerced into that range:
	 * - [Long.MIN_VALUE] is returned if it's less than `Long.MIN_VALUE`,
	 * - [Long.MAX_VALUE] is returned if it's greater than `Long.MAX_VALUE`.
	 *
	 * An infinite duration value is converted either to [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
	 */
	public val inWholeGameNanoseconds: Long
		get() = impl.inWholeNanoseconds

	// context(scheduler: Scheduler)
	// public fun toDuration(): Duration = impl * scheduler.engineSecond.toDouble(DurationUnit.SECONDS)

	override fun toString(): String = "$impl [in-engine]"

	public companion object {
		public val Zero: EngineDuration = EngineDuration(Duration.ZERO)
		public val Infinite: EngineDuration = EngineDuration(Duration.INFINITE)
	}
}

/**
 * Returns a [Duration] equal to this [Int] number of the specified [unit].
 *
 * @since 0.0.1-alpha.4
 */
public fun Int.toEngineDuration(unit: DurationUnit): EngineDuration = EngineDuration(toDuration(unit))

/**
 * Returns a [Duration] equal to this [Long] number of the specified [unit].
 *
 * @since 0.0.1-alpha.4
 */
public fun Long.toEngineDuration(unit: DurationUnit): EngineDuration = EngineDuration(toDuration(unit))

/**
 * Returns a [Duration] equal to this [Double] number of the specified [unit].
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this `Double` value is `NaN`.
 * @since 0.0.1-alpha.4
 */
public fun Double.toEngineDuration(unit: DurationUnit): EngineDuration = EngineDuration(toDuration(unit))

/**
 * Returns an [EngineDuration] equal to this [Int] number of engine nanoseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Int.engineNanoseconds: EngineDuration get() = toEngineDuration(DurationUnit.NANOSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Long] number of engine nanoseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Long.engineNanoseconds: EngineDuration get() = toEngineDuration(DurationUnit.NANOSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Double] number of engine nanoseconds.
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this [Double] value is `NaN`.
 * @since 0.0.1-alpha.4
 */
public inline val Double.engineNanoseconds: EngineDuration get() = toEngineDuration(DurationUnit.NANOSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Int] number of engine microseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Int.engineMicroseconds: EngineDuration get() = toEngineDuration(DurationUnit.MICROSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Long] number of engine microseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Long.engineMicroseconds: EngineDuration get() = toEngineDuration(DurationUnit.MICROSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Double] number of engine microseconds.
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this [Double] value is `NaN`.
 * @sinces 0.0.1-alpha.4
 */
public inline val Double.engineMicroseconds: EngineDuration get() = toEngineDuration(DurationUnit.MICROSECONDS)

/**
 * Returns an [EngineDuration] equal to this [Int] number of engine milliseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Int.engineMilliseconds: EngineDuration get() = toEngineDuration(DurationUnit.MILLISECONDS)

/**
 * Returns an [EngineDuration] equal to this [Long] number of engine milliseconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Long.engineMilliseconds: EngineDuration get() = toEngineDuration(DurationUnit.MILLISECONDS)

/**
 * Returns an [EngineDuration] equal to this [Double] number of engine milliseconds.
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this [Double] value is `NaN`.
 * @since 0.0.1-alpha.4
 */
public inline val Double.engineMilliseconds: EngineDuration get() = toEngineDuration(DurationUnit.MILLISECONDS)

/**
 * Returns an [EngineDuration] equal to this [Int] number of engine seconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Int.engineSeconds: EngineDuration get() = toEngineDuration(DurationUnit.SECONDS)

/**
 * Returns an [EngineDuration] equal to this [Long] number of engine seconds.
 *
 * @since 0.0.1-alpha.4
 */
public inline val Long.engineSeconds: EngineDuration get() = toEngineDuration(DurationUnit.SECONDS)

/**
 * Returns an [EngineDuration] equal to this [Double] number of engine seconds.
 *
 * Depending on its magnitude, the value is rounded to an integer number of nanoseconds or milliseconds.
 *
 * @throws IllegalArgumentException if this [Double] value is `NaN`.
 * @since 0.0.1-alpha.4
 */
public inline val Double.engineSeconds: EngineDuration get() = toEngineDuration(DurationUnit.SECONDS)
