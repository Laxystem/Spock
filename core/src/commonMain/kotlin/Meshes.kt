package quest.laxla.spock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

public val <Vertex : Any> Mesh<Vertex>.orderedVertices: Flow<Vertex>
	get() = indices.asFlow().map { vertices[it.toInt()] }
