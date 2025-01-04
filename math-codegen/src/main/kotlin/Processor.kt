package quest.laxla.spock.math.codegen

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.collections.immutable.toImmutableList
import quest.laxla.spock.math.VectorOperation

public class Processor(
	public val generator: CodeGenerator,
	public val logger: KSPLogger,
	public val generateBuiltIns: Boolean,
) : SymbolProcessor {
	override fun process(resolver: Resolver): List<KSAnnotated> {
		if (generateBuiltIns) try {
			generateSwizzlers().writeTo(generator, false)
		} catch (e: FileAlreadyExistsException) {
			logger.exception(e)
		}

		try {
			resolver.generateVectorOperations().writeTo(
				codeGenerator = generator,
				aggregating = true,
				originatingKSFiles = resolver.getSymbolsWithAnnotation(VectorOperation::class.qualifiedName!!)
					.mapNotNull { it as? KSFile ?: it.containingFile }.toImmutableList()
			)
		} catch (e: FileAlreadyExistsException) {
			logger.exception(e)
		}

		return emptyList()
	}
}