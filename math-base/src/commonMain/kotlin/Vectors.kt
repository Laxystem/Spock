package quest.laxla.spock.math

import kotlin.jvm.JvmInline

private data class TypedVector1<V, out S>(override val space: S, override val x: V) : Vector1<V, S> where S : Space<V>

/**
 * Creates a [single-element vector][Vector1].
 *
 * @since 0.0.1-alpha.4
 * @see Space.x
 */
public fun <V, S> S.vectorOf(x: V): Vector1<V, S> where S : Space<V> = TypedVector1(this, x)

private data class TypedVector2<V, out S>(
	override val space: S,
	override val x: V,
	override val y: V
) : Vector2<V, S> where S : Space<V>

/**
 * Creates a [two-element vector][Vector2].
 *
 * @since 0.0.1-alpha.4
 * @see Space.xy
 * @see Vector1.y
 */
public fun <V, S> S.vectorOf(x: V, y: V): Vector2<V, S> where S : Space<V> = TypedVector2(this, x, y)

private data class TypedVector3<V, out S>(
	override val space: S,
	override val x: V,
	override val y: V,
	override val z: V
) : Vector3<V, S> where S : Space<V>

/**
 * Creates a [three-element vector][Vector4].
 *
 * @since 0.0.1-alpha.4
 * @see Space.xyz
 * @see Vector2.z
 */
public fun <V, S> S.vectorOf(x: V, y: V, z: V): Vector3<V, S> where S : Space<V> = TypedVector3(this, x, y, z)

private data class TypedVector4<V, out S>(
	override val space: S,
	override val x: V,
	override val y: V,
	override val z: V,
	override val w: V
) : Vector4<V, S> where S : Space<V>

/**
 * Creates a [four-element vector][Vector4].
 *
 * @since 0.0.1-alpha.4
 * @see Space.xyzw
 * @see Vector3.w
 */
public fun <V, S> S.vectorOf(x: V, y: V, z: V, w: V): Vector4<V, S> where S : Space<V> = TypedVector4(this, x, y, z, w)

/**
 * Creates a [single-element vector][Vector1] containing [x].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> S.x(x: V): Vector1<V, S> where S : Space<V> = TypedVector1(this, x)

/**
 * Creates a [Vector2] whose elements are both [xy].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> S.xy(xy: V): Vector2<V, S> where S : Space<V> = vectorOf(xy, xy)

/**
 * Creates a [Vector3] whose elements are all [xyz].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> S.xyz(xyz: V): Vector3<V, S> where S : Space<V> = vectorOf(xyz, xyz, xyz)

/**
 * Creates a [Vector4] whose elements are all [xyzw].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> S.xyzw(xyzw: V): Vector4<V, S> where S : Space<V> = vectorOf(xyzw, xyzw, xyzw, xyzw)

/**
 * Creates a [Vector2] whose first element is taken from [this], with [y] as its second.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector1<V, S>.y(y: V): Vector2<V, S> where S : Space<V> = space.vectorOf(x, y)

/**
 * Creates a [Vector3] whose first element is taken from [this], with its second and third taken from [yz].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector1<V, S>.yz(yz: Vector2<V, S>): Vector3<V, S> where S : Space<V> =
	space.vectorOf(x, yz.x, yz.y)

/**
 * Creates a [Vector4] whose first element is taken from [this], with [yz] as its second and third.
 */
public infix fun <V, S> Vector1<V, S>.yz(yz: V): Vector3<V, S> where S : Space<V> = space.vectorOf(x, yz, yz)

/**
 * Creates a [Vector4] whose first element is taken from [this], with its second, third, and fourth taken from [yzw].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector1<V, S>.yzw(yzw: Vector3<V, S>): Vector4<V, S> where S : Space<V> =
	space.vectorOf(x, yzw.x, yzw.y, yzw.z)

/**
 * Creates a [Vector4] whose first element is taken from [this], with [yzw] as its second, third, and fourth.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector1<V, S>.yzw(yzw: V): Vector4<V, S> where S : Space<V> =
	space.vectorOf(x, yzw, yzw, yzw)

/**
 * Creates a [Vector3] whose first and second elements are taken from [this], with [z] as its third.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector2<V, S>.z(z: V): Vector3<V, S> where S : Space<V> = space.vectorOf(x, y, z)

/**
 * Creates a [Vector4] whose first and second elements are taken from [this], with its third and fourth taken from [zw].
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector2<V, S>.zw(zw: Vector2<V, S>): Vector4<V, S> where S : Space<V> =
	space.vectorOf(x, y, zw.x, zw.y)

/**
 * Creates a [Vector4] whose first and second elements are taken from [this], with [zw] as its third and fourth.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector2<V, S>.zw(zw: V): Vector4<V, S> where S : Space<V> =
	space.vectorOf(x, y, zw, zw)

/**
 * Creates a [Vector4] whose first, second and third elements are taken from [this], with [w] as its fourth.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun <V, S> Vector3<V, S>.w(w: V): Vector4<V, S> where S : Space<V> = space.vectorOf(x, y, z, w)

private fun UniformVector<*, *>.stringify() = "$space($value...)"

private data class TypedUniformVector<V, out S>(
	override val space: S,
	override val value: V
) : UniformVector<V, S> where S : Space<V> {
	override fun toString(): String = stringify()
}

/**
 * Creates a [UniformVector] whose elements are all equal to [value].
 * 
 * @since 0.0.1-alpha.4
 * @see vectorOfZeros
 * @see vectorOfOnes
 */
public fun <V, S> S.uniformVectorOf(value: V): UniformVector<V, S> where S : Space<V> = TypedUniformVector(this, value)

@JvmInline
private value class TypedZeroVector<V, S>(override val space: S) : UniformVector<V, S> where S : Space<V> {
	override val value: V get() = space.zero

	override fun toString(): String = stringify()
}

/**
 * Creates a [UniformVector] of [Space.zero]s.
 *
 * @since 0.0.1-alpha.4
 * @see uniformVectorOf
 * @see vectorOfOnes
 */
public fun <V, S> S.vectorOfZeros(): UniformVector<V, S> where S : Space<V> = TypedZeroVector(this)

@JvmInline
private value class TypedUnitVector<V, S>(override val space: S) : UniformVector<V, S> where S : Space<V> {
	override val value: V get() = space.unit

	override fun toString(): String = stringify()
}

/**
 * Creates a [UniformVector] of [Space.unit]s.
 *
 * @since 0.0.1-alpha.4
 * @see uniformVectorOf
 * @see vectorOfZeros
 */
public fun <V, S> S.vectorOfOnes(): UniformVector<V, S> where S : Space<V> = TypedUnitVector(this)

/**
 * Creates a [Vector1] whose elements all equal to [this.value][UniformVector.value].
 *
 * @since 0.0.1-alpha.4
 */
public fun <V, S> UniformVector<V, S>.asVector1() where S : Space<V> = space x value

/**
 * Creates a [Vector1] whose elements all equal to [this.value][UniformVector.value].
 *
 * @since 0.0.1-alpha.4
 */
public fun <V, S> UniformVector<V, S>.asVector2() where S : Space<V> = space.vectorOf(value, value)

/**
 * Creates a [Vector1] whose elements all equal to [this.value][UniformVector.value].
 *
 * @since 0.0.1-alpha.4
 */
public fun <V, S> UniformVector<V, S>.asVector3() where S : Space<V> = space.vectorOf(value, value, value)

/**
 * Creates a [Vector1] whose elements all equal to [this.value][UniformVector.value].
 *
 * @since 0.0.1-alpha.4
 */
public fun <V, S> UniformVector<V, S>.asVector4() where S : Space<V> = space.vectorOf(value, value, value, value)

/**
 * Retrieves the first element of this vector.
 *
 * @since 0.0.1-alpha.4
 * @see VectorWithX.x
 */
public operator fun <V> VectorWithX<V, *>.component1() = x

/**
 * Retrieves the second element of this vector.
 *
 * @since 0.0.1-alpha.4
 * @see VectorWithY.y
 */
public operator fun <V> VectorWithY<V, *>.component2() = y

/**
 * Retrieves the third element of this vector.
 *
 * @since 0.0.1-alpha.4
 * @see VectorWithZ.z
 */
public operator fun <V> VectorWithZ<V, *>.component3() = z

/**
 * Retrieves the fourth element of this vector.
 *
 * @since 0.0.1-alpha.4
 * @see VectorWithW.w
 */
public operator fun <V> VectorWithW<V, *>.component4() = w
