package quest.laxla.spock

import io.ygdrasil.wgpu.Device
import io.ygdrasil.wgpu.Surface
import io.ygdrasil.wgpu.TextureFormat

public interface WebGpuApplication : Application {
	public val device: Device
	public val surface: Surface
	public val format: TextureFormat
}
