package quest.laxla.spock.toolkit

import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.Surface
import io.ygdrasil.wgpu.TextureFormat
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.Renderer

@SubclassOptInRequired(ExperimentalSpockApi::class)
public interface WebGpuRenderer : Renderer {
	public val device: Device
	public val surface: Surface
	public val format: TextureFormat
}
