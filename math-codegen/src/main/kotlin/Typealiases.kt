package quest.laxla.spock.math.codegen

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import quest.laxla.spock.math.Typealiased

public fun Resolver.getTypealiasedClassDeclarations() = getSymbolsWithAnnotation<Typealiased>()
	.filterIsInstance<KSClassDeclaration>()
	.filterNot { it.superTypes.count() == 0 }

private val serializerFunctionName = MemberName("kotlinx.serialization", "serializer")

@OptIn(KspExperimental::class)
public fun Resolver.generateTypealiases(packageName: String): FileSpec = file(packageName, "Typealiases") {
	addFileComment("\nnot using OptIn as it cannot be referenced in the code generator")

	addAnnotation(annotation<Suppress> {
		addMember(format = "%S", "OPT_IN_USAGE_ERROR")
	})

	for (space in getTypealiasedClassDeclarations()) {
		val annotation = space.getAnnotations<Typealiased>().single()
		val type = try {
			annotation.type.asClassName()
		} catch (e: KSTypeNotPresentException) {
			e.ksType.toClassName()
		}

		val spaceType = space.toClassName()

		for (tier in VectorTier.entries) {
			val name = annotation.name.replace("#", tier.exactClass.simpleName!!)
			val vectorClass = tier.exactClass.asClassName().parameterizedBy(type, spaceType)

			val serializer = objectType(name + "Serializer") {
				addSuperinterface(
					KSerializer::class.asClassName().parameterizedBy(vectorClass),
					delegate = CodeBlock.of(
						format = "%T.serializer(%S, %T, %M<%T>())",
						tier.exactClass,
						"$packageName.$name",
						spaceType,
						serializerFunctionName,
						type
					)
				)
			}

			typeAlias(name, vectorClass.copy(annotations = listOf(annotation<Serializable> {
				addMember(format = "%N::class", serializer)
			})))
		}
	}
}
