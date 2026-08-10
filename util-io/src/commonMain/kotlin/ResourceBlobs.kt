package quest.laxla.spock.io

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.io.Source
import kotlinx.io.readByteString
import quest.laxla.spock.ByteString
import quest.laxla.spock.IO

private val log = KotlinLogging.logger { }

private class SourceResourceBlob(source: RawSource) : ResourceBlob {
	private val sourceJunction = source.junction()
	private val value = lazy {
		val result = ResourceBlob.Immediate(sourceJunction.split().readByteString())
		sourceJunction.solidify()

		result
	}

	override val isFullyAcquired: Boolean get() = value.isInitialized() || sourceJunction.isExhausted

	override fun asSource(): Source = if (value.isInitialized()) value.value.asSource() else try {
		sourceJunction.split()
	} catch (e: IllegalStateException) {
		log.warn(e) { "Resource blob source junction has solidified despite not being fully acquired, " +
				"or we've accidentally caught a lower-level exception (thanks, Java); " +
				"Attempting to fully acquire blob, WARNING: this might IO-block the current thread as we cannot suspend here, report this bug!" }

		try {
			value.value.asSource()
		} catch (e2 : Throwable) {
			e2.addSuppressed(e)
			log.error(e2) { "Failed acquiring resource blob whilst trying to recover from a failed source junction split" }
			throw e
		}
	}

	// If resource blobs were Jobs, we would be awaiting them here.
	// We dispatch to suspend the thread instead of blocking it, as some dispatchers are sensitive to this.
	override suspend fun acquire() = withContext(Dispatchers.IO) {
		value.value
	}

	override suspend fun toByteString(): ByteString = acquire().toByteString()

	override suspend fun intoBuffer(): Buffer = acquire().intoBuffer()

	override suspend fun toByteArray(): ByteArray = acquire().toByteArray()
}

/**
 * Creates a [ResourceBlob] containing the data within this [Source].
 *
 * The ownership of the source, including the responsibility to close it, is passed onto the returning blob,
 * which is ***not*** required to immediately read the source; To do so, instead create a [ResourceBlob.Immediate] via [Source.readByteString].
 *
 * @since 0.0.1-alpha.4
 */
public fun RawSource.asResourceBlob(): ResourceBlob = SourceResourceBlob(this)
