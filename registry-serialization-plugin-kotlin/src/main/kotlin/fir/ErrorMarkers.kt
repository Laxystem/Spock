package quest.laxla.spock.serialization.plugin.kotlin.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactory0
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataKey
import org.jetbrains.kotlin.fir.declarations.FirDeclarationDataRegistry
import org.jetbrains.kotlin.fir.declarations.FirProperty
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private fun booleanErrorMarker(key: FirDeclarationDataKey): ReadWriteProperty<FirDeclaration, Boolean> {
	val accessor = FirDeclarationDataRegistry.data(key)

	return object : ReadWriteProperty<FirDeclaration, Boolean> {
		override fun getValue(thisRef: FirDeclaration, property: KProperty<*>): Boolean =
			accessor.getValue<Unit?>(thisRef, property) != null

		override fun setValue(thisRef: FirDeclaration, property: KProperty<*>, value: Boolean) {
			accessor.setValue(thisRef, property, value = if (value) Unit else null)
		}
	}
}

private data object UnclassifiableReturnTypeKey : FirDeclarationDataKey()
private data object TopLevelPropertiesUnclassifiableKey : FirDeclarationDataKey()

var FirProperty.isReturnTypeUnclassifiable: Boolean by booleanErrorMarker(UnclassifiableReturnTypeKey)
var FirProperty.isClassifiedAtTopLevel: Boolean by booleanErrorMarker(TopLevelPropertiesUnclassifiableKey)


@OptIn(ExperimentalContracts::class)
context(
	context: CheckerContext,
	reporter: DiagnosticReporter
)
internal inline fun <D : FirDeclaration> D.report(
	diagnosticFactory: KtDiagnosticFactory0,
	block: D.() -> Boolean
) {
	contract {
		callsInPlace(block, InvocationKind.EXACTLY_ONCE)
	}

	if (block()) reporter.reportOn(source, diagnosticFactory, context)
}
