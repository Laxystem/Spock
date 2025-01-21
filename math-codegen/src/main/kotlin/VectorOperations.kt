package quest.laxla.spock.math.codegen

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.*
import quest.laxla.spock.ExperimentalSpockApi
import quest.laxla.spock.at
import quest.laxla.spock.math.*

public fun Resolver.getVectorOperations() = getSymbolsWithAnnotation<VectorOperation>()
	.filterIsInstance<KSFunctionDeclaration>()

public val KSAnnotated.vectorization: Vectorization.Kind
	get() = getAllAnnotations<Vectorization>().firstOrNull()?.kind ?: Vectorization.Kind.Disabled

// TODO: fix this whateverness
@OptIn(KspExperimental::class, ExperimentalSpockApi::class)
public fun Resolver.generateVectorOperations(packageName: String): FileSpec = file(packageName, "VectorOperations") {
	val space = getDeclarationOf(Space::class)!!.asStarProjectedType()

	for (operation in getVectorOperations()) {
		val parentDeclaration = operation.parentDeclaration as? KSClassDeclaration ?: continue
		if (!space.isAssignableFrom(parentDeclaration.asStarProjectedType())) continue
		val parentDeclarationClassName = parentDeclaration.toClassName()

		val subject = parentDeclaration.typeParameters.singleOrNull() ?: parentDeclaration.typeParameters.single {
			it.isAnnotationPresent(VectorSubject::class)
		}

		val typeParameters = (operation.typeParameters + (operation.parentDeclaration?.typeParameters
			?: emptyList())).toTypeParameterResolver()

		val subjectType = subject.toTypeVariableName(typeParameters)
		val subjectSpaceType = parentDeclarationClassName.asSpaceTypeParameter(subjectType)

		val spaceProvider = operation.parameters.singleOrNull {
			it.vectorization != Vectorization.Kind.Disabled && it.isAnnotationPresent(SpaceProvider::class)
		}

		val returnType = operation.returnType?.toTypeName(typeParameters)
		val returnTypeDeclaration = operation.returnType?.resolve()?.declaration
		val returnSpaceType = when {
			returnType == null -> null
			returnTypeDeclaration == subject -> subjectSpaceType
			else -> parentDeclarationClassName.parameterizedBy(returnType)
		}

		val receiverVectorization = operation.extensionReceiver?.vectorization
		val receiverType = operation.extensionReceiver?.toTypeName(typeParameters)
		val receiverTypeDeclaration = operation.extensionReceiver?.resolve()?.declaration
		val receiverSpaceType = when (receiverTypeDeclaration) {
			null -> null
			subject -> subjectSpaceType
			returnTypeDeclaration -> returnSpaceType
			else -> parentDeclarationClassName.parameterizedBy(receiverType!!)
		}

		val parameterTypes = operation.parameters.map {
			when (it.type.resolve().declaration) {
				subject -> subjectType to subjectSpaceType
				returnTypeDeclaration -> returnType!! to returnSpaceType!!
				receiverTypeDeclaration -> receiverType!! to receiverSpaceType!!
				else -> {
					val parameterType = it.type.toTypeName(typeParameters)
					parameterType to parentDeclarationClassName.parameterizedBy(parameterType)
				}
			}
		}
		val parameterVectorization = operation.parameters.map(KSAnnotated::vectorization)

		for ((tierIndex, tier) in VectorTier.entries.withIndex()) function(operation.simpleName.getShortName()) {
			addTypeVariable(subjectType)
			addTypeVariable(subjectSpaceType)
			for (parameter in operation.typeParameters) if (parameter != subject) addTypeVariable(parameter.toTypeVariableName())

			if (returnType != null) returns(
				tier.exactClass.asClassName().parameterizedBy(returnType, returnSpaceType!!)
			)

			val exactClass = tier.exactClass.asClassName()
			val minimumClass = tier.minimumClass.asClassName()

			when (receiverVectorization) {
				Vectorization.Kind.Exact -> receiver(exactClass.parameterizedBy(receiverType!!, receiverSpaceType!!))
				Vectorization.Kind.Minimum -> receiver(
					minimumClass.parameterizedBy(
						receiverType!!,
						receiverSpaceType!!
					)
				)

				Vectorization.Kind.Disabled -> receiver(receiverType!!)
				null -> {}
			}

			for (modifier in operation.modifiers) modifier.toKModifier()?.let { addModifiers(it) }
			operation.getVisibility().toKModifier()?.let { addModifiers(it) }

			for ((index, parameter) in operation.parameters.withIndex()) {
				parameters.getOrNull(index)?.let(::addParameter) ?: parameter(
					name = parameter.name?.asString()!!,
					returnType = when (parameterVectorization[index]) {
						Vectorization.Kind.Exact -> parameterTypes[index].let { (type, spaceType) ->
							exactClass.parameterizedBy(type, spaceType)
						}

						Vectorization.Kind.Minimum -> parameterTypes[index].let { (type, spaceType) ->
							minimumClass.parameterizedBy(type, spaceType)
						}

						Vectorization.Kind.Disabled -> parameterTypes[index].first
					}
				) {
					if (parameter.isCrossInline) addModifiers(KModifier.CROSSINLINE)
					if (parameter.isVararg) addModifiers(KModifier.VARARG)
					if (parameter.isNoInline) addModifiers(KModifier.NOINLINE)
				}
			}

			val spaceProviderName = spaceProvider?.run { name!! }?.getShortName() ?: "this"

			addStatement(
				(0..tierIndex at VectorTier.entries).joinToString(
					prefix = "return with($spaceProviderName.space) { vectorOf(",
					separator = ", ",
					postfix = ") }"
				) { tier ->
					buildString {
						append("${tier.property.name} = ")
						if (receiverType != null) append(tier.property.name + '.')
						append(operation.simpleName.getShortName())

						parameters.asSequence().mapIndexed { index, parameter ->
							parameter.name + if (parameterVectorization[index] != Vectorization.Kind.Disabled) '.' + tier.property.name else ""
						}.joinToString(prefix = "(", separator = ", ", postfix = ")").let(::append)
					}
				}
			)
		}
	}
}
