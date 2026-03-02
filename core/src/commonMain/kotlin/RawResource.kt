package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable

@Serializable
public data class RawResource(
	override val name: Name,
	val properties: ImmutableMap<String, String>,
	val blobs: ImmutableMap<String, ByteString>
) : Named {
	public operator fun <T> get(name: Name, deserializer: DeserializationStrategy<T>): T {
		return
	}

	public inline operator fun <reified T> get(name: Name) {

	}

}
