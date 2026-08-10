package quest.laxla.spock.serialization.plugin.kotlin.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SpockFirExtensionRegistrar : FirExtensionRegistrar() {
	override fun ExtensionRegistrarContext.configurePlugin() {
		+::ClassifierFirStatusTransformerExtension
	}
}