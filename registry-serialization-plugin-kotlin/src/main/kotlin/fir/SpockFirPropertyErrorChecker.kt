package quest.laxla.spock.serialization.plugin.kotlin.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty

object SpockFirPropertyErrorChecker : FirDeclarationChecker<FirProperty>(MppCheckerKind.Common) {
	context(
		context: CheckerContext,
		reporter: DiagnosticReporter
	)
	override fun check(declaration: FirProperty) {
		declaration.report(SpockFirErrors.UNCLASSIFIABLE_TYPE, FirProperty::isReturnTypeUnclassifiable)
		declaration.report(SpockFirErrors.UNCLASSIFIABLE_TOP_LEVEL, FirProperty::isClassifiedAtTopLevel)
	}
}