package quest.laxla.spock.math.codegen

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import quest.laxla.spock.math.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal typealias VectorKClass = KClass<out VectorWithX<*, *>>
internal typealias VectorKProperty<V> = KProperty1<out VectorWithX<V, Space<V>>, V>

public enum class VectorTier(
	public val exactClass: VectorKClass,
	public val minimumClass: VectorKClass,
	public val spacelessClass: KClass<*>,
	vararg properties: VectorKProperty<*>
) {
	//@formatter:off
	X(Vector1::class, VectorWithX::class, SpacelessVector1::class, VectorWithX<*, *>::x),
	Y(Vector2::class, VectorWithY::class, SpacelessVector2::class, VectorWithY<*, *>::y),
	Z(Vector3::class, VectorWithZ::class, SpacelessVector3::class, VectorWithY<*, *>::y, VectorWithZ<*, *>::z),
	W(Vector4::class, VectorWithW::class, SpacelessVector4::class, VectorWithY<*, *>::y, VectorWithZ<*, *>::z, VectorWithW<*, *>::w);
	//@formatter:on

	public val properties: ImmutableList<VectorKProperty<*>> = properties.toImmutableList()
	public val property: VectorKProperty<*> = properties.last()
}
