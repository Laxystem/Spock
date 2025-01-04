package quest.laxla.spock.math.codegen

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import quest.laxla.spock.math.Space
import quest.laxla.spock.math.Vector1
import quest.laxla.spock.math.Vector2
import quest.laxla.spock.math.Vector3
import quest.laxla.spock.math.Vector4
import quest.laxla.spock.math.VectorWithW
import quest.laxla.spock.math.VectorWithX
import quest.laxla.spock.math.VectorWithY
import quest.laxla.spock.math.VectorWithZ
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

internal typealias VectorKClass = KClass<out VectorWithX<*, *>>
internal typealias VectorKProperty<V> = KProperty1<out VectorWithX<V, Space<V>>, V>

public enum class VectorTier(
	public val exactClass: VectorKClass,
	public val minimumClass: VectorKClass,
	vararg properties: VectorKProperty<*>
) {
	X(Vector1::class, VectorWithX::class, VectorWithX<*, *>::x),
	Y(Vector2::class, VectorWithY::class, VectorWithX<*, *>::x, VectorWithY<*, *>::y),
	Z(Vector3::class, VectorWithZ::class, VectorWithX<*, *>::x, VectorWithY<*, *>::y, VectorWithZ<*, *>::z),
	W(Vector4::class, VectorWithW::class, VectorWithX<*, *>::x, VectorWithY<*, *>::y, VectorWithZ<*, *>::z, VectorWithW<*, *>::w);
	
	public val properties: ImmutableList<VectorKProperty<*>> = properties.toImmutableList()
	public val property: VectorKProperty<*> = properties.last()
}
