package quest.laxla.spock

import kotlinx.io.bytestring.ByteString
import kotlinx.io.bytestring.ByteStringBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * @author Laxystem
 */
@OptIn(ExperimentalSpockApi::class)
private class ByteAppenderImpl(private val buffer: ByteArray, override var offset: Int) : ByteAppender {
	private var asByteString: ByteString? = null

	override val capacity: Int
		get() = buffer.size

	override val remainingCapacity: Int
		get() = capacity - offset

	override fun append(byte: Byte) {
		asByteString = null
		buffer[offset++] = byte
	}

	override fun append(array: ByteArray, startIndexInclusive: Int, endIndexExclusive: Int) {
		require(startIndexInclusive <= endIndexExclusive) { "startIndexInclusive ($startIndexInclusive) > endIndexExclusive ($endIndexExclusive)" }
		if (startIndexInclusive < 0 || endIndexExclusive > array.size) throw IndexOutOfBoundsException(
			"startIndexInclusive ($startIndexInclusive) and endIndexExclusive ($endIndexExclusive) represents " +
					"an interval out of array's bounds [0..${array.size})."
		)

		asByteString = null
		array.copyInto(buffer, offset, startIndexInclusive, endIndexExclusive)
		offset += endIndexExclusive - startIndexInclusive
	}

	override fun toByteString(): ByteString = asByteString ?: ByteString(buffer).also { asByteString = it }
}

/**
 * Creates a [ByteAppender] beginning at the provided [offset] of this [ByteArray].
 *
 * @since 0.0.1-alpha.4
 * @see ByteAppender.offset
 */
public fun ByteArray.appender(offset: Int = 0): ByteAppender = ByteAppenderImpl(this, offset)

/**
 * Creates a [ByteAppender] of the provided [size].
 *
 * @since 0.0.1-alpha.4
 */
public fun ByteAppender(size: Int): ByteAppender = ByteArray(size).appender()

/**
 * Creates a [ByteString] of the provided [size], populated via a [ByteAppender].
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun appendToByteString(size: Int, block: ByteAppender.() -> Unit): ByteString {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return ByteAppender(size).apply(block).toByteString()
}

/**
 * Creates a [ByteArray] of the provided [size], and populates it via a [ByteAppender].
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun appendToByteArray(size: Int, block: ByteAppender.() -> Unit): ByteArray {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	return ByteArray(size).apply { appender().block() }
}

/**
 * Populates this [ByteArray] by appending onto it starting at the provided [offset].
 *
 * @see ByteArray.appender
 * @see ByteAppender.offset
 *
 * @since 0.0.1-alpha.4
 */
@OptIn(ExperimentalContracts::class)
public inline fun ByteArray.append(offset: Int = 0, block: ByteAppender.() -> Unit) {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	appender(offset).block()
}

/**
 * @author Laxystem
 */
@JvmInline
@OptIn(ExperimentalSpockApi::class)
private value class WrappedByteStringBuilder(private val builder: ByteStringBuilder) : ByteAppender {
	override val offset: Int
		get() = builder.size
	override val capacity: Int
		get() = builder.capacity
	override val remainingCapacity: Int
		get() = Int.MAX_VALUE - offset

	override fun append(byte: Byte) {
		builder.append(byte)
	}

	override fun append(array: ByteArray, startIndexInclusive: Int, endIndexExclusive: Int) {
		builder.append(array)
	}

	override fun toByteString(): ByteString = builder.toByteString()
}

/**
 * Wraps this [ByteStringBuilder] to be compatible with the [appendToByteString] API.
 *
 * @since 0.0.1-alpha.4
 */
public fun ByteStringBuilder.asAppender(): ByteAppender = WrappedByteStringBuilder(this)

/**
 * Appends onto this [ByteStringBuilder] via the [ByteAppender] interface.
 *
 * @since 0.0.1-alpha.4
 */
public inline fun ByteStringBuilder.append(block: ByteAppender.() -> Unit): Unit = asAppender().block()
