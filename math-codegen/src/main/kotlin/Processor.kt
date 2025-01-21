package quest.laxla.spock.math.codegen

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import quest.laxla.spock.math.Typealiased
import quest.laxla.spock.math.VectorOperation

private fun Sequence<KSAnnotated>.toFileList(): ImmutableList<KSFile> =
	mapNotNull { it as? KSFile ?: it.containingFile }.toImmutableList()

private inline fun <T> ignoreFileAlreadyExistsException(block: () -> T) = try {
	block()
} catch (_: FileAlreadyExistsException) {
}

public class Processor(
	public val generator: CodeGenerator,
	public val logger: KSPLogger,
	public val packageName: String
) : SymbolProcessor {

	private fun FileSpec.write() = ignoreFileAlreadyExistsException { writeTo(generator, aggregating = false) }
	private fun FileSpec.write(originatingKSFiles: List<KSFile>) = ignoreFileAlreadyExistsException {
		writeTo(generator, true, originatingKSFiles)
	}

	override fun process(resolver: Resolver): List<KSAnnotated> {
		if (packageName == MathPackage) generateSwizzlers().write()

		resolver.generateVectorOperations(packageName)
			.write(originatingKSFiles = resolver.getSymbolsWithAnnotation<VectorOperation>().toFileList())

		resolver.generateTypealiases(packageName)
			.write(originatingKSFiles = resolver.getSymbolsWithAnnotation<Typealiased>().toFileList())

		return emptyList()
	}
}
