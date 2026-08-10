package quest.laxla.spock.registry

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.core.AbsoluteName
import quest.laxla.spock.core.Namespace
import quest.laxla.spock.core.get

/**
 * Describes a category of resources.
 *
 * Can have three forms;
 * * Concrete [ResourceType], for fully transferrable resources;
 * * [Reference]s to [ResourceType]s that cannot be acquired at bootstrap time;
 * * References to an uninferrable resource type; I.e. describing resources that cannot be transferred at all.
 *
 * @param E the associated registry's data type.
 * @since 0.0.1-alpha.4
 */
public typealias ResourceClassifier<E> = AcquirableReference<ResourceType<E>, ResourceType<*>>

/**
 * Describes a specific kind of resource, e.g. a PNG image.
 *
 * @since 0.0.1-alpha.4
 */
public interface ResourceType<E : Any> : RegistryObject<ResourceType<E>, ResourceType<*>> {
	/**
	 * Specifies the general category of a resource.
	 *
	 * Used to transfer resources over HTTP, to optimize internal resource handling,
	 * and to allow interop with OS-specific features.
	 *
	 * Note this enum might gain additional entries if IANA were to introduce additional discrete MIME types.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public enum class Kind(public val mediaType: String) {
		/**
		 * Audio or music data.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Audio(mediaType = "audio"),

		/**
		 * Font/typeface data.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Font(mediaType = "font"),

		/**
		 * Image or graphical data including both bitmap and vector still images
		 * as well as animated versions of still image formats such as animated GIF or APNG.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Image(mediaType = "image"),

		/**
		 * Model data for a 3D object or scene.
		 */
		Model(mediaType = "model"),

		/**
		 * Text-only data including any human-readable content, source code,
		 * or unstructured, textual data such as comma-separated value (CSV) formatted data.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Text(mediaType = "text"),

		/**
		 * Video data.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Video(mediaType = "video"),

		/**
		 * Kotlin objects transferred over text or binary formats.
		 *
		 * In the future, such content types of the form `application/example` might be replaced with `application/spock+example`;
		 * Thus, It is not equivalent to [Other].
		 *
		 * @since 0.0.1-alpha.4
		 */
		Object(mediaType = "application"),

		/**
		 * Other forms of data, e.g. PDF files.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Other(mediaType = "application");

		/**
		 * @since 0.0.1-alpha.4
		 */
		public companion object {
			/**
			 * @since 0.0.1-alpha.4
			 */
			public const val RAW_BYTES_SUBTYPE: String = "octet-stream"
		}
	}

	/**
	 * The general category of the resource.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val kind: Kind

	/**
	 * The MIME content subtype of this resource type.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val contentSubtype: String

	/**
	 * Collapses several resources of this type into one.
	 *
	 * The provided flow is non-empty, sorted by [ResourceFormat]-version,
	 * containing only the entries with the highest (and thus, an equal) [ResourceVersion.revision],
	 * as all others are dropped by nature.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun Flow<Registry.Entry<E>>.collapse(): E = first().resource

	/**
	 * The [ResourceType] of all resource types.
	 *
	 *     One type to rule them all,
	 *       One type to find them,
	 *     One type to bring them all,
	 *       and in the darkness bind them.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public companion object Root : ResourceType<ResourceType<*>>, KSerializer<ResourceClassifier<*>> {
		override val kind: Kind get() = Kind.Text
		override val contentSubtype: String get() = "plain" // TODO: reconsider

		override val name: AbsoluteName = Namespace.Engine["resource_type"]

		override val descriptor: SerialDescriptor = SerialDescriptor(
			serialName = name.toDotFormattedString()!!,
			original = Key.descriptor
		)

		override fun serialize(encoder: Encoder, value: ResourceClassifier<*>) {
			Key.serialize(encoder, value.toKeyOrThrow())
		}

		@Suppress("UNCHECKED_CAST")
		override fun deserialize(decoder: Decoder): ResourceClassifier<*> =
			Key.deserialize(decoder) as ResourceClassifier<*>
	}

	override val classifier: ResourceClassifier<ResourceType<*>> get() = Root
	override fun get(): ResourceType<E> = this
}
