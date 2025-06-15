package quest.laxla.spock.toolkit

import io.ygdrasil.webgpu.GPUShaderModuleCompilationHint
import io.ygdrasil.webgpu.GPUShaderModuleDescriptor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Version of [io.ygdrasil.webgpu.ShaderModuleDescriptor] that does not contain [code] as part of [toString],
 * for better logging.
 *
 * @since 0.0.1-alpha.4
 */
public data class ShaderModuleDescriptor(
	override val code: String,
	override val compilationHints: ImmutableList<GPUShaderModuleCompilationHint> = persistentListOf(),
	override val label: String = ""
) : GPUShaderModuleDescriptor {
	override fun toString(): String = buildString {
		append("ShaderModuleDescriptor(compilationHints=$compilationHints")
		if (label.isNotBlank()) append(", label = $label")
		append(")")
	}
}
