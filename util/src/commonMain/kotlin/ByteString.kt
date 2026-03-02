package quest.laxla.spock

import kotlinx.io.bytestring.ByteString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public typealias ByteString = @Serializable(ByteStringSerializer::class) ByteString

/**
 * @since 0.0.1-alpha.4
 * @author Laxystem
 */
public object ByteStringSerializer : KSerializer<ByteString> {
	private val original = ByteArraySerializer()
	override val descriptor: SerialDescriptor
		get() = SerialDescriptor("quest.laxla.spock.ByteStringSerializer", original.descriptor)

	override fun serialize(encoder: Encoder, value: ByteString) {
		encoder.encodeSerializableValue(original, value.toByteArray())
	}

	override fun deserialize(decoder: Decoder): ByteString = ByteString(decoder.decodeSerializableValue(original))
}
