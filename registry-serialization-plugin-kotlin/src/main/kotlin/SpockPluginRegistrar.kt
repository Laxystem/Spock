package quest.laxla.spock.serialization.plugin.kotlin

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import quest.laxla.spock.registry.serialization.plugin.pluginName
import quest.laxla.spock.serialization.plugin.kotlin.fir.SpockFirExtensionRegistrar

class SpockPluginRegistrar : CompilerPluginRegistrar() {
	override val pluginId: String get() = pluginName
	override val supportsK2: Boolean get() = true

	override fun ExtensionStorage.registerExtensions(
		configuration: CompilerConfiguration
	) {
		FirExtensionRegistrarAdapter.registerExtension(SpockFirExtensionRegistrar())
	}

}