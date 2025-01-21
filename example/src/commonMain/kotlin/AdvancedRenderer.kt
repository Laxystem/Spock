@file:Suppress("UnusedImport")

package quest.laxla.spock.example

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.ShaderModule
import io.ygdrasil.webgpu.ShaderModuleDescriptor
import io.ygdrasil.webgpu.TextureFormat
import kotlinx.io.bytestring.buildByteString
import quest.laxla.spock.*
import quest.laxla.spock.toolkit.Surface
import quest.laxla.spock.toolkit.WebGpuRenderer

@Suppress("unused")
private val logger = KotlinLogging.logger {}
/*
@OptIn(ExperimentalSpockApi::class)
public class AdvancedRenderer<Vertex : Any>(
	override val device: Device,
	override val surface: Surface,
	override val format: TextureFormat = TODO(),
	public val scene: RenderScene<Vertex>,
	public val vertexKind: VertexKind<Vertex>
) : WebGpuRenderer, Closer by Closer(surface, device) {
	private val shaders = +MultiCache<ShaderModuleDescriptor, ShaderModule>(device::createShaderModule)
	private val vertexBuffers = mutableMapOf<ShaderModuleDescriptor, Pair<ByteArray, List<Vertex>>>()

	private fun Sequence<Pipeline<Vertex>>.toMeshMap() = this
		.filter { (_, shader) -> shader is WgslShader && shader.vertexKind == vertexKind }
		.groupingBy(Pipeline<Vertex>::vertexShader)
		.aggregate { shader, list: MutableList<Vertex>?, (mesh), _ ->
			if (mesh is VertexMesh<Vertex>) list?.apply { addAll(mesh.vertices) } ?: mesh.vertices.toMutableList()
			else list ?: mutableListOf()
		}

	override suspend fun invoke() {
		for ((shader, vertices) in scene.pipelines.toMeshMap()) {
			val shaderModule = shaders[(shader as WgslShader).shaderModule]

			*//*
			val vertexBuffer = vertexBuffers[shader.shaderModule]
			if (vertexBuffer != null && vertexBuffer.second.size == vertices.size) {
				for ((index, vertex) in vertices.withIndex()) if (vertex != vertexBuffer.second[index]) ByteStringBuilder(vertexBuffer.first, index * vertexKind.sizeInBytes)
			} else {

			vertexBuffers[shader.shaderModule] = *//* val byteString = buildByteString {
				with(vertexKind) {
					for (vertex in vertices) append(vertex)
				}
			}.toByteArray() *//* to vertices
			} *//*
		}
	}
}*/
