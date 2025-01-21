package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * @author Laxystem
 */
private data class SimpleMesh<Vertex : Any>(
	override val vertices: ImmutableList<Vertex>,
	override val indices: ImmutableList<UInt>
) : Mesh<Vertex>

/**
 * Creates a new constant mesh using the given [vertices] and [indices].
 *
 * @param indices the order of indices in this mesh. If empty, vertices will be used in the order they're provided.
 *
 * @since 0.0.1-alpha.4
 */
public fun <Vertex : Any> meshOf(
	vertices: @FutureImmutableArray ImmutableList<Vertex>,
	indices: @FutureImmutableArray ImmutableList<UInt> = persistentListOf<UInt>()
): Mesh<Vertex> = SimpleMesh(vertices, indices)
