package quest.laxla.spock.math

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import quest.laxla.spock.RawSpockApi

/**
 * @param V [Vector] type
 * @param S [Space]
 * @param Vec [Vector]
 * @param NoS Spaceless [Vector]
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 */
private inline fun <V, S, Vec, NoS> createVectorSerializer(
	name: String,
	impl: KSerializer<V>,
	spacelessSerializer: KSerializer<NoS>,
	crossinline withSpace: NoS.() -> Vec,
	crossinline withoutSpace: Vec.() -> NoS
): KSerializer<Vec> where S : Space<V>, Vec : Vector<V, S> = object : KSerializer<Vec> {
	override val descriptor: SerialDescriptor = SerialDescriptor(name, impl.descriptor)

	override fun deserialize(decoder: Decoder): Vec =
		decoder.decodeSerializableValue(spacelessSerializer).withSpace()

	override fun serialize(encoder: Encoder, value: Vec) =
		encoder.encodeSerializableValue(spacelessSerializer, value.withoutSpace())
}

/**
 * Returns a version of this [Vector1] without its [space][Vector.space].
 *
 * @since 0.0.1-alpha.4
 */

@RawSpockApi
public fun <V> Vector1<V, *>.withoutSpace() = SpacelessVector1(x)

/**
 * Creates a [KSerializer] for [Vector1]s of the given [Space].
 *
 * It is recommended to use [Typealiased] on a space instead.
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V, S> Vector1.Companion.serializer(
	name: String,
	space: S,
	impl: KSerializer<V>
): KSerializer<Vector1<V, S>> where S : Space<V> = createVectorSerializer(
	name,
	impl,
	SpacelessVector1.serializer(impl),
	withSpace = { space(space) },
	withoutSpace = { withoutSpace() }
)

/**
 * Returns a version of this [Vector1] without its [space][Vector.space].
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V> Vector2<V, *>.withoutSpace() = SpacelessVector2(x, y)

/**
 * Creates a [KSerializer] for [Vector2]s of the given [Space].
 *
 * It is recommended to use [Typealiased] on a space instead.
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V, S> Vector2.Companion.serializer(
	name: String,
	space: S,
	impl: KSerializer<V>
): KSerializer<Vector2<V, S>> where S : Space<V> = createVectorSerializer(
	name,
	impl,
	SpacelessVector2.serializer(impl),
	withSpace = { space(space) },
	withoutSpace = { withoutSpace() }
)

/**
 * Returns a version of this [Vector1] without its [space][Vector.space].
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V> Vector3<V, *>.withoutSpace() = SpacelessVector3(x, y, z)

/**
 * Creates a [KSerializer] for [Vector3]s of the given [Space].
 *
 * It is recommended to use [Typealiased] on a space instead.
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V, S> Vector3.Companion.serializer(
	name: String,
	space: S,
	impl: KSerializer<V>
): KSerializer<Vector3<V, S>> where S : Space<V> = createVectorSerializer(
	name,
	impl,
	SpacelessVector3.serializer(impl),
	withSpace = { space(space) },
	withoutSpace = { withoutSpace() }
)

/**
 * Returns a version of this [Vector1] without its [space][Vector.space].
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V> Vector4<V, *>.withoutSpace() = SpacelessVector4(x, y, z, w)

/**
 * Creates a [KSerializer] for [Vector3]s of the given [Space].
 *
 * It is recommended to use [Typealiased] on a space instead.
 *
 * @since 0.0.1-alpha.4
 */
@RawSpockApi
public fun <V, S> Vector4.Companion.serializer(
	name: String,
	space: S,
	impl: KSerializer<V>
): KSerializer<Vector4<V, S>> where S : Space<V> = createVectorSerializer(
	name,
	impl,
	SpacelessVector4.serializer(impl),
	withSpace = { space(space) },
	withoutSpace = { withoutSpace() }
)
