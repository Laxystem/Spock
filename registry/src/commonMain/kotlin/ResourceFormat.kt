package quest.laxla.spock.registry

import io.github.z4kn4fein.semver.Version
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy

/**
 * Handles encoding and decoding resources.
 *
 * @since 0.0.1-alpha.4
 */
public sealed interface ResourceFormat<T : Any> {
	/**
	 * The type of resources this format handles.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val resourceType: ResourceType<T>

	/**
	 * The resource format version.
	 *
	 * ### Version compatibility rules
	 * 1. Any format must be capable of deserializing outputs of versions of the same [major][io.github.z4kn4fein.semver.Version.major],
	 * the same or lower [minor][io.github.z4kn4fein.semver.Version.minor], and any [patch][io.github.z4kn4fein.semver.Version.patch] (that is, of the form, `x.<=y.*`).
	 * 2. Formats whose `x` is `0` have relaxed requirements;
	 *    They need only be capable of deserializing outputs of versions of the same [major][io.github.z4kn4fein.semver.Version.major] and [minor][io.github.z4kn4fein.semver.Version.minor],
	 *    and the same or lower [patch][io.github.z4kn4fein.semver.Version.patch] (that is, of the form `0.y.<=z`).
	 * 3. [io.github.z4kn4fein.semver.Version.buildMetadata] (the `x.y.z+build` part) is ignored for this purpose.
	 * 4. [Pre-releases][io.github.z4kn4fein.semver.Version.isPreRelease] (versions with the `x.y.z-prerelease` part) must comply with the same requirements as full releases,
	 *    but no version is ever required to be capable of deserializing the output of a pre-release, except the pre-release itself.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val version: Version

	public interface Serial<T : Any> : ResourceFormat<T> {
		public val serialFormat: SerialFormat

		public suspend fun readFrom(source: Source, deserializationStrategy: DeserializationStrategy<T>): T

		public suspend fun writeInto(value: T, serializationStrategy: SerializationStrategy<T>, sink: Sink)
	}

	public interface Nonserial<T : Any> : ResourceFormat<T> {
		/**
		 * Reads a resource out of the provided [source] and closes it.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public suspend fun readFrom(source: Source): T

		/**
		 * Writes [value] into the provided [sink].
		 *
		 * @since 0.0.1-alpha.4
		 */
		public suspend fun writeInto(value: T, sink: Sink)
	}
}