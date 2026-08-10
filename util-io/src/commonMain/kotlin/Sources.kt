package quest.laxla.spock.io

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.io.*
import quest.laxla.spock.Flag
import quest.laxla.spock.set

private class SourceJunctionImpl(private val source: RawSource, vararg initialBuffers: Buffer) : SynchronizedObject(), SourceJunction {
	private val isClosed = Flag()
	private val isSourceClosed = Flag()
	private val isSolidified = Flag()
	private val junctionBuffer = Buffer()
	private var index: Long = 0
	private val subBuffers = mutableSetOf(*initialBuffers)

	private fun closeSource() = isSourceClosed.set {
		source.close()
	}

	private fun _readAtMost(byteCount: Long): Long = synchronized(this) {
		val readBytes = source.readAtMostTo(junctionBuffer, byteCount)

		if (readBytes == -1L) {
			closeSource()

			return -1
		}

		val readSegmentEndIndex = index + readBytes
		for (subBuffer in subBuffers) junctionBuffer.copyTo(subBuffer, index, readSegmentEndIndex)

		index = readSegmentEndIndex

		return readBytes
	}

	private fun ensureNotClosed() {
		if (isClosed()) error("Source junction is closed")
	}

	override fun readAtMost(byteCount: Long): Long {
		ensureNotClosed()
		return _readAtMost(byteCount)
	}

	override fun solidify() = isSolidified.set()


	private fun Buffer.closeSubbuffer() {
		subBuffers.remove(this)

		if (isSolidified() && subBuffers.isEmpty()) close()
	}

	private inner class SplitSource : RawSource {
		var buffer: Buffer? = null

		init {
			ensureNotClosed()
		}

		override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
			ensureNotClosed()
			if (buffer == null) buffer = sink
			else error("Split source was unexpectedly exposed to an unrecognized buffer!")
			require(buffer in subBuffers) { "Split source is closed" }
			require(byteCount >= 0) { "Byte count must not be negative" }

			return _readAtMost(byteCount)
		}

		override fun close() {
			buffer?.closeSubbuffer()
			buffer = null
		}
	}

	private fun ensureNotSolidified() {
		ensureNotClosed()
		if (isSolidified()) error("Source junction has solidified, and cannot be split.")
	}

	@OptIn(InternalIoApi::class)
	override fun split(): Source {
		ensureNotSolidified()

		return SplitSource().buffered().also {
			junctionBuffer.copyTo(it.buffer)
			subBuffers += it.buffer
		}
	}

	override fun close() = isClosed.set {
		closeSource()
		junctionBuffer.close()
		subBuffers.clear()
	}
}

/**
 * Creates a [SourceJunction] out of this [Source].
 *
 * Whenever [readAtMostTo] is called on any source downstream from a returned [SourceJunction],
 * the call will be delegated to the provided, upstream source, and its result cached into an internal buffer,
 * then copied into *all* downstream sources' [buffer][Source.buffer]s.
 *
 * The resulting [SourceJunction] can be [split][SourceJunction.split] thread-safely, but not [closed][Source.close].
 * Instead, it is possibly to [solidify][SourceJunction] the junction, so that it closes automatically once all downstream sources close.
 *
 * This approach is non-expensive, as KotlinX IO *already* requires a [Buffer] to wrap [RawSource] into [Source]s.
 *
 * @since 0.0.1-alpha.4
 */
public fun RawSource.junction(): SourceJunction = SourceJunctionImpl(this)
