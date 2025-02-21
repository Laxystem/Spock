package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents a [Mesh], that is, [vertices] and [indices] to be rendered on the GPU via a [Pipeline].
 *
 * Meshes may not necessarily contain the actual vertices to be rendered on the screen;
 * They might be passed through a mesh shader or into a compute shader.
 *
 * @since 0.0.1-alpha.1
 */
public data class Mesh<out Vertex : Any>(
	/**
	 * The actual data of this mesh.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val vertices: @FutureImmutableArray ImmutableList<Vertex>,

	/**
	 * Stores the order in which [vertices] will be accounted for by the GPU,
	 * deduplicating data transfer and massively improving performance.
	 *
	 * For example, given the below quad and the triangles `ABD` and `ADC`,
	 * ```
	 * A--B
	 * |\ |
	 * | \|
	 * C--D
	 * ```
	 *
	 * Normal rendering would need to send six vertices to the GPU: `A, B, D, A, D, C`.
	 *
	 * Indexed rendering instead sends only four vertices, `A, B, C, D`, and indices, `0, 1, 3, 0, 3, 2`.
	 * Given vectors that consist of a [three-dimensional 32bit position][quest.laxla.spock.math.Vector3f]
	 * and a vertex color (that consists of four 32bit numbers, red, green, blue, and alpha),
	 * indexed rendering saves 48 bytes and only requires 8 more, that is, 40 bytes saved
	 * even with only six vertices.
	 *
	 * Indexed rendering's efficiency scales exponentially the more complex the mesh is.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val indices: @FutureImmutableArray ImmutableList<UInt> = persistentListOf()
)
