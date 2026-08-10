package quest.laxla.spock.registry.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.core.AbsoluteName
import quest.laxla.spock.core.Named
import quest.laxla.spock.registry.*

/**
 * @author Laxystem
 */
private class KeySerializer<E : Any>(val classifier: ResourceClassifier<E>) : KSerializer<ResourceKey<E>> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
		"quest.laxla.spock.registry.serialization.Key",
		PrimitiveKind.STRING
	)

	override fun deserialize(decoder: Decoder): ResourceKey<E> {
		val string = decoder.decodeString()
		val classifierName = string.substringBefore(Key.SEPARATOR)

		val name = AbsoluteName(
			if (classifierName == string) string
			else {
				require(AbsoluteName(classifierName) == classifier.name) { "Expected classifier [${classifier.name}], got [$string]" }

				string.substringAfter(Key.SEPARATOR)
			}
		)

		return Key(classifier, name)
	}

	override fun serialize(encoder: Encoder, value: ResourceKey<E>) {
		encoder.encodeString(value.name.toString())
	}
}

/**
 * @since 0.0.1-alpha.4
 */
public fun <E : Any> ResourceClassifier<E>.keySerializer(): KSerializer<ResourceKey<E>> = KeySerializer(classifier = this)

private class BoxSerializer<E : Any>(
	val classifier: ResourceClassifier<E>,
	private val impl: KSerializer<E>
) : KSerializer<ResourceBox<E>> {
	override val descriptor: SerialDescriptor = SerialDescriptor(
		"quest.laxla.spock.registry.serialization.Box",
		original = impl.descriptor
	)

	override fun deserialize(decoder: Decoder): ResourceBox<E> =
		classifier.reference(decoder.decodeSerializableValue(impl))

	override fun serialize(encoder: Encoder, value: ResourceBox<E>) {
		encoder.encodeSerializableValue(impl, value.get())
	}
}

/**
 * @since 0.0.1-alpha.4
 */
public fun <E : Any> ResourceClassifier<E>.boxSerializer(impl: KSerializer<E>): KSerializer<ResourceBox<E>> =
	BoxSerializer(classifier = this, impl)

@Serializable
private data class RegistrationPacket<E : Any>(
	val resource: E,
	override val name: AbsoluteName = (resource as Named).name
) : Named {
	constructor(registration: Registration<E, E>) : this(
		resource = registration.get(),
		name = registration.name
	)
}

private class RegistrationSerializer<E : Any>(
	val classifier: ResourceClassifier<E>,
	impl: KSerializer<E>
) : KSerializer<Registration<E, E>> {
	val impl = RegistrationPacket.serializer(impl)

	override val descriptor: SerialDescriptor = SerialDescriptor(
		serialName = "quest.laxla.spock.registry.serialization.Registration",
		original = impl.descriptor
	)

	override fun deserialize(decoder: Decoder): Registration<E, E> {
		val packet = decoder.decodeSerializableValue(impl)

		return Registration(
			key = Key(classifier, packet.name),
			resource = packet.resource
		)
	}
	override fun serialize(encoder: Encoder, value: Registration<E, E>) {
		encoder.encodeSerializableValue(impl, RegistrationPacket(value))
	}
}

public fun <E : Any> ResourceClassifier<E>.registrationSerializer(impl: KSerializer<E>): KSerializer<Registration<E, E>> =
	RegistrationSerializer(classifier = this, impl = impl)
