package quest.laxla.spock.io

import kotlinx.io.Buffer
import kotlinx.io.IOException
import kotlinx.io.Source
import quest.laxla.spock.ByteString
import kotlin.jvm.JvmInline

/**
 * Represents an immutable, possibly not-yet-acquired, blob of data.
 *
 * @since 0.0.1-alpha.4
 */
public interface ResourceBlob {
	/**
	 * Has this resource been fully acquired?
	 *
	 * @since 0.0.1-alpha.4
	 */
	@get:Throws(IOException::class)
	public val isFullyAcquired: Boolean

	/**
	 * Acquires this resource blob as a [Source], allowing it to be streamed.
	 *
	 * It is safe for multiple sources to be acquired from the same [ResourceBlob] concurrently.
	 * It is the caller's responsibility to [close][Source.close] the returned source,
	 * and to handle any IO thread blocks that it might cause.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun asSource(): Source

	/**
	 * Fully acquires the blob.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IOException::class)
	public suspend fun acquire(): Immediate

	/**
	 * Fully acquires the blob and returns it as a [kotlinx.io.bytestring.ByteString].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IOException::class)
	public suspend fun toByteString(): ByteString = acquire().toByteString()

	/**
	 * Fully acquires the blob and returns it as a [Buffer].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IOException::class)
	public suspend fun intoBuffer(): Buffer = acquire().intoBuffer()

	/**
	 * Fully acquires the blob and returns it as a [ByteArray].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Throws(IOException::class)
	public suspend fun toByteArray(): ByteArray = acquire().toByteArray()

	/**
	 * Fully-acquired [ResourceBlob].
	 *
	 * @since 0.0.1-alpha.4
	 */
	@JvmInline
	public value class Immediate(private val byteString: ByteString) : ResourceBlob {
		override val isFullyAcquired: Boolean get() = true

		override fun asSource(): Source = byteString.intoBuffer()

		override suspend fun acquire(): Immediate = this

		override suspend fun toByteString(): ByteString = byteString

		override suspend fun intoBuffer(): Buffer = byteString.intoBuffer()

		override suspend fun toByteArray(): ByteArray = byteString.toByteArray()
	}
}
