package quest.laxla.spock.core

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import quest.laxla.spock.io.PrefixedProperties
import kotlin.jvm.JvmInline

private val logger = KotlinLogging.logger { }

/**
 * Stores and parses metadata about a resource.
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
public value class ResourceMetadata(public val metadata: ImmutableMap<String, String>) {
	/**
	 * Acquires a [Property] property about this resource.
	 *
	 * @since 0.0.1-alpha.4
	 */
	@Suppress("KotlinUnreachableCode") // TODO: why is this even spawned on TODO?
	@OptIn(ExperimentalSerializationApi::class)
	public operator fun <T> get(metadataProperty: Property<T>): T = try {
		PrefixedProperties.decodeFromStringMap(
			deserializer = metadataProperty.serializer,
			map = metadata,
			prefix = TODO("this used to say metadataProperty.prefix.")
		)
	} catch (e: SerializationException) {
		if (metadataProperty.warnMissing) logger.warn(e) { "Failed deserializing property [${metadataProperty.name}] from resource [${metadataProperty.name}]" }
		metadataProperty.default
	}
}