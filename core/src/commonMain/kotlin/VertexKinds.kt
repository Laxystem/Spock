package quest.laxla.spock

import kotlinx.io.bytestring.ByteStringBuilder
import quest.laxla.spock.math.*

/**
 * Creates a simple [VertexKind] for [Vector1]s of this space.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see vertexKindOfVector2
 * @see vertexKindOfVector3
 * @see vertexKindOfVector4
 */
public fun <V, S> S.vertexKindOfVector1(): VertexKind<Vector1<V, S>> where S : BufferableSpace<V> =
	object : VertexKind<Vector1<V, S>> {
		override val sizeInBytes: UInt
			get() = this@vertexKindOfVector1.sizeInBytes

		override fun ByteStringBuilder.append(vertex: Vector1<V, S>) = append(vector = vertex)

		override fun toString(): String = "Vector1 of ${this@vertexKindOfVector1}"
	}

/**
 * Creates a simple [VertexKind] for [Vector2]s of this space.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see vertexKindOfVector1
 * @see vertexKindOfVector3
 * @see vertexKindOfVector4
 */
public fun <V, S> S.vertexKindOfVector2(): VertexKind<Vector2<V, S>> where S : BufferableSpace<V> =
	object : VertexKind<Vector2<V, S>> {
		override val sizeInBytes: UInt
			get() = this@vertexKindOfVector2.sizeInBytes

		override fun ByteStringBuilder.append(vertex: Vector2<V, S>) = append(vector = vertex)

		override fun toString(): String = "Vector2 of ${this@vertexKindOfVector2}"
	}

/**
 * Creates a simple [VertexKind] for [Vector3]s of this space.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see vertexKindOfVector1
 * @see vertexKindOfVector2
 * @see vertexKindOfVector4
 */
public fun <V, S> S.vertexKindOfVector3(): VertexKind<Vector3<V, S>> where S : BufferableSpace<V> =
	object : VertexKind<Vector3<V, S>> {
		override val sizeInBytes: UInt
			get() = this@vertexKindOfVector3.sizeInBytes

		override fun ByteStringBuilder.append(vertex: Vector3<V, S>) = append(vector = vertex)

		override fun toString(): String = "Vector3 of ${this@vertexKindOfVector3}"
	}

/**
 * Creates a simple [VertexKind] for [Vector4]s of this space.
 *
 * @author Laxystem
 * @since 0.0.1-alpha.4
 * @see vertexKindOfVector1
 * @see vertexKindOfVector2
 * @see vertexKindOfVector3
 */
public fun <V, S> S.vertexKindOfVector4(): VertexKind<Vector4<V, S>> where S : BufferableSpace<V> =
	object : VertexKind<Vector4<V, S>> {
		override val sizeInBytes: UInt
			get() = this@vertexKindOfVector4.sizeInBytes

		override fun ByteStringBuilder.append(vertex: Vector4<V, S>) = append(vector = vertex)

		override fun toString(): String = "Vector4 of ${this@vertexKindOfVector4}"
	}
