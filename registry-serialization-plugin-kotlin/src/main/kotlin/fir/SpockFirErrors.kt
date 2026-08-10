package quest.laxla.spock.serialization.plugin.kotlin.fir

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error0
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.psi.KtDeclaration

object SpockFirErrors : KtDiagnosticsContainer() {
	val UNCLASSIFIABLE_TYPE by error0<KtDeclaration>(SourceElementPositioningStrategies.DECLARATION_RETURN_TYPE)
	val UNCLASSIFIABLE_TOP_LEVEL by error0<KtDeclaration>()

	override fun getRendererFactory(): BaseDiagnosticRendererFactory = Messages


	object Messages : BaseDiagnosticRendererFactory() {
		override val MAP: KtDiagnosticFactoryToRendererMap by KtDiagnosticFactoryToRendererMap("Spock FIR Errors") {
			it.put(UNCLASSIFIABLE_TYPE, "Type cannot be classified")
			it.put(UNCLASSIFIABLE_TOP_LEVEL, "Top-level properties cannot be annotated with @Classifier")
		}
	}
}
