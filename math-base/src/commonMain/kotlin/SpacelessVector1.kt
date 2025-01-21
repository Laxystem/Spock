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
import quest.laxla.spock.Definitive
import quest.laxla.spock.RawSpockApi
import kotlin.jvm.JvmInline

/**
 * Represents a [Vector1] without referencing a [Space].
 *
 * @since 0.0.1-alpha.4
 */
@Definitive(since = "0.0.1-alpha.4")
@JvmInline
@RawSpockApi
@Serializable(SpacelessVector1.Serializer::class)
public value class SpacelessVector1<V>(public val x: V) {
	/**
	 * Creates a proper [Vector1] with the provided [Space].
	 *
	 * @since 0.0.1-alpha.4
	 */
	public infix fun <S> space(space: S): Vector1<V, S> where S : Space<V> = space.vectorOf(x)

	/**
	 * @since 0.0.1-alpha.4
	 * @see x
	 */
	public operator fun component1(): V = x

	/**
	 * @author Laxystem
	 * @since 0.0.1-alpha.4
	 */
	public class Serializer<V>(private val impl: KSerializer<V>) : KSerializer<SpacelessVector1<V>> {
		@OptIn(InternalSerializationApi::class)
		override val descriptor: SerialDescriptor = buildSerialDescriptor(
			SpacelessVector1::class.qualifiedName!!,
			StructureKind.LIST,
			impl.descriptor
		) {
			element("x", impl.descriptor)
		}

		@OptIn(ExperimentalSerializationApi::class)
		override fun deserialize(decoder: Decoder): SpacelessVector1<V> = decoder.decodeStructure(descriptor) {
			var x: Any? = NotAssignedError

			if (decodeSequentially()) x = decodeSerializableElement(descriptor, 0, impl)
			else while (true) when (val index = decodeElementIndex(descriptor)) {
				0 -> x = decodeSerializableElement(descriptor, index, impl)
				DECODE_DONE -> break
				else -> error("did not expect index $index")
			}

			@Suppress("UNCHECKED_CAST")
			SpacelessVector1(x as V)
		}

		override fun serialize(
			encoder: Encoder,
			value: SpacelessVector1<V>
		): Unit = encoder.encodeStructure(descriptor) {
			encodeSerializableElement(descriptor, 0, impl, value.x)
		}

		override fun equals(other: Any?): Boolean = other is Serializer<V> && impl == other.impl
		override fun hashCode(): Int = impl.hashCode()
	}
}
