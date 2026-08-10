package quest.laxla.spock.registry.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.registry.Registration
import quest.laxla.spock.registry.ResourceBox
import quest.laxla.spock.registry.ResourceKey

// TODO: compiler plugin to detect uses of @Classifier, replace with NOOP in FIR, replace with actual in IR

private fun noop(): Nothing =
	throw NotImplementedError("This serializer should've been patched out by the Spock IR plugin.")

@InternalNoopSerializer
public class KeyNoopSerializer<E : Any> : KSerializer<ResourceKey<E>> {
	override val descriptor: Nothing get() = noop()

	override fun serialize(encoder: Encoder, value: ResourceKey<E>): Nothing = noop()

	override fun deserialize(decoder: Decoder): Nothing = noop()
}

@InternalNoopSerializer
public class BoxNoopSerializer<E : Any>(impl: KSerializer<E>) : KSerializer<ResourceBox<E>> {
	override val descriptor: Nothing get() = noop()

	override fun serialize(
		encoder: Encoder,
		value: ResourceBox<E>
	): Nothing = noop()

	override fun deserialize(decoder: Decoder): Nothing = noop()
}

@InternalNoopSerializer
public class RegistrationNoopSerializer<E : Any>(impl: KSerializer<E>) : KSerializer<Registration<E, E>> {
	override val descriptor: Nothing get() = noop()

	override fun serialize(
		encoder: Encoder,
		value: Registration<E, E>
	): Nothing = noop()

	override fun deserialize(decoder: Decoder): Nothing = noop()
}
