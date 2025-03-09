package quest.laxla.spock.example

import io.ygdrasil.webgpu.VertexFormat
import io.ygdrasil.webgpu.sizeInBytes
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.sync.Mutex
import quest.laxla.spock.*
import quest.laxla.spock.math.IntSpace.append

internal fun ByteArrayPool(mutex: Mutex = Mutex()) = Pool(
	::ByteArray,
	ByteArray::size,
	mutex = mutex
)

internal class MeshStorage<in Vertex : Any>(
	override val vertexKind: VertexKind<Vertex>,
	private val pool: InferringCache<Int, ByteArray> = ByteArrayPool()
) : VertexKind.Bound<Vertex>, SuspendCloseable {
	private var vertexBuffer: ByteArray? = null
	private val vertexIndices = hashMapOf<Vertex, Int>()
	private var previousMeshes: ImmutableSet<Mesh<Vertex>>? = null

	private val vertexSize = vertexKind.attributes.sumOf(VertexFormat::sizeInBytes)

	fun reconstructIndices(mesh: Mesh<Vertex>): ByteArray = appendToByteArray(mesh.indices.size) {
		for (index in mesh.indices) append(
			vertexIndices[mesh.vertices[index.toInt()]]
				?: throw IllegalArgumentException("Vertex ${mesh.vertices[index.toInt()]} at index $index not found in mesh storage; This is an engine error.")
		)
	}

	private suspend fun update(meshes: ImmutableSet<Mesh<Vertex>>) {
		val vertices = meshes.flatMapTo(hashSetOf()) { it.vertices }
		val newBufferSize = vertices.size * vertexSize
		if (newBufferSize == vertexBuffer?.size) {
			val obsoleteVertices = vertexIndices.keys.filterTo(mutableListOf()) { it !in vertices }

			with(vertexKind) {
				for (vertex in vertices) if (vertex !in vertexIndices) {
					val oldVertex = obsoleteVertices.removeFirst()
					val index = vertexIndices.remove(oldVertex)!!
					vertexBuffer!!.appender(index).append(vertex)
					vertexIndices[vertex] = index
				}
			}
		} else {
			vertexIndices.clear()
			vertexBuffer?.let { pool.putReferenceTo(it) }
			vertexBuffer = pool[newBufferSize]

			vertexBuffer!!.append {
				with(vertexKind) {
					for (vertex in vertices) if (vertex !in vertexIndices) {
						vertexIndices[vertex] = offset
						append(vertex)
					}
				}
			}
		}

		previousMeshes = meshes
	}

	suspend operator fun get(meshes: Collection<Mesh<Vertex>>): ByteArray {
		if (meshes != previousMeshes) update(meshes.toImmutableSet())
		return vertexBuffer!!
	}

	override suspend fun close() {
		vertexIndices.clear()
		vertexBuffer?.let { pool.put(it) }
		vertexBuffer = null
	}
}
