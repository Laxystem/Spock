package quest.laxla.spock.math

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import quest.laxla.spock.RawSpockApi

/**
 * Represents a [Vector2] without referencing a [Space].
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
@Serializable(SpacelessVector2.Serializer::class)
public data class SpacelessVector2<V>(public val x: V, public val y: V) {
	/**
	 * Creates a proper [Vector2] with the provided [Space].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public infix fun <S> space(space: S): Vector2<V, S> where S : Space<V> = space.vectorOf(x, y)

	/**
	 * @author Laxystem
	 * @since 0.0.1-alpha.4
	 */
	public class Serializer<V>(private val impl: KSerializer<V>) : KSerializer<SpacelessVector2<V>> {
		@OptIn(InternalSerializationApi::class)
		override val descriptor: SerialDescriptor = buildSerialDescriptor(
			SpacelessVector2::class.qualifiedName!!,
			StructureKind.LIST,
			impl.descriptor
		) {
			element("x", impl.descriptor)
			element("y", impl.descriptor)
		}

		@OptIn(ExperimentalSerializationApi::class)
		override fun deserialize(decoder: Decoder): SpacelessVector2<V> = decoder.decodeStructure(descriptor) {
			var x: Any? = NotAssignedError
			var y: Any? = NotAssignedError

			if (decodeSequentially()) {
				x = decodeSerializableElement(descriptor, 0, impl)
				y = decodeSerializableElement(descriptor, 1, impl)
			} else while (true) when (val index = decodeElementIndex(descriptor)) {
				0 -> x = decodeSerializableElement(descriptor, index, impl)
				1 -> y = decodeSerializableElement(descriptor, index, impl)
				DECODE_DONE -> break
				else -> error("did not expect index $index")
			}

			@Suppress("UNCHECKED_CAST")
			SpacelessVector2(x as V, y as V)
		}

		override fun serialize(
			encoder: Encoder,
			value: SpacelessVector2<V>
		): Unit = encoder.encodeStructure(descriptor) {
			encodeSerializableElement(descriptor, 0, impl, value.x)
			encodeSerializableElement(descriptor, 1, impl, value.y)
		}

		override fun equals(other: Any?): Boolean = other is Serializer<V> && impl == other.impl
		override fun hashCode(): Int = impl.hashCode()
	}
}
