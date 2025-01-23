package quest.laxla.spock.example

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.ShaderModuleDescriptor
import io.ygdrasil.webgpu.TextureFormat
import quest.laxla.spock.*
import quest.laxla.spock.toolkit.Surface
import quest.laxla.spock.toolkit.WebGpuRenderer

private val logger = KotlinLogging.logger {}

@OptIn(ExperimentalSpockApi::class)
public class AdvancedRenderer<Vertex : Any>(
	override val device: Device,
	override val surface: Surface,
	override val format: TextureFormat = surface.textureFormat,
	public val scene: RenderScene<Vertex>,
	public val vertexKind: VertexKind<Vertex>,
	transpiler: Shader.Transpiler.Simple<Wgsl, StringShader.FormFactor>
) : WebGpuRenderer, Closer by Closer(surface, device) {
	private val shaders = +ShaderCache(transpiler)
	private val shaderDescriptors = +ManualCache<Shader, ShaderModuleDescriptor> {
		ShaderModuleDescriptor(
			code = (it as StringShader).code,
			label = it.label,
			compilationHints = listOf(ShaderModuleDescriptor.CompilationHint(entryPoint = (it as Wgsl.Shader).entrypoint))
		)
	}
	private val shaderModules = +ManualCache(device::createShaderModule)

	private val vertexBuffers = mutableMapOf<ShaderModuleDescriptor, Pair<ByteArray, List<Vertex>>>()

	override suspend fun invoke() {
		TODO()
	}
}
