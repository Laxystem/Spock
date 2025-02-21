package quest.laxla.spock

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentHashSetOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap

private val logger = KotlinLogging.logger {}

/**
 * Creates a readable [String] about this [Shader] that does not contain the shader's actual implementation.
 *
 * @since 0.0.1-alpha.4
 * @see transpilationTraceToString
 */
public val Shader.readableName
	get() = buildString {
		append(formFactor)
		append('(')
		append("language = $language")
		if (label != null) append(", label = \"$label\"")
		append(')')
	}

/**
 * @author Laxystem
 */
private fun StringBuilder.appendTranspilationTrace(shader: Shader) {
	if (shader is Shader.Transpiled<*, *>) {
		appendTranspilationTrace(shader.original)
		append(" =>> ")
	}

	append(shader.readableName)
}

/**
 * Creates a readable [String] about this [Shader],
 * including what shaders it was transpiled from, if any.
 *
 * @since 0.0.1-alpha.4
 * @see readableName
 */
public fun Shader.transpilationTraceToString(): String = buildString {
	appendTranspilationTrace(this@transpilationTraceToString)
}

/**
 * Does this [Shader] carry all metadata
 * required by the [language][Shader.language] and [form factor][Shader.formFactor]
 * it is written in?
 *
 * @since 0.0.1-alpha.4
 * @see Shader.Language.accepts
 * @see Shader.FormFactor.accepts
 * @see Shader.Kind.accepts
 */
public val Shader.isCarryingRequiredMetadata: Boolean get() = language accepts this && formFactor accepts this

/**
 * Throws if this [Shader] does not carry all required metadata.
 *
 * @since 0.0.1-alpha.4
 * @see isCarryingRequiredMetadata
 */
@Throws(UnsupportedShaderException::class)
public fun <S : Shader> S.throwingIfMissingMetadata(): S = also {
	if (!(language accepts it)) throw UnsupportedShaderException(it, "missing metadata required by language")
	if (!(formFactor accepts it)) throw UnsupportedShaderException(it, "missing metadata required by form factor")
}

/**
 * Does the provided [shader] carry all metadata required by this [Shader.Kind]?
 *
 * @since 0.0.1-alpha.4
 * @see Shader.Language.accepts
 * @see Shader.FormFactor.accepts
 * @see Shader.isOfKind
 * @see Shader.isCarryingRequiredMetadata
 */
public infix fun Shader.Kind<*, *>.accepts(shader: Shader) = language accepts shader && formFactor accepts shader

/**
 * Does this [Shader] belong to this shader [kind]?
 *
 * Note this does not imply this shader [is carrying all required metadata][Shader.Kind.accepts].
 *
 * @since 0.0.1-alpha.4
 * @see Shader.language
 * @see Shader.formFactor
 */
public infix fun Shader.isOfKind(kind: Shader.Kind<*, *>): Boolean =
	language == kind.language && formFactor == kind.formFactor

/**
 * The [Shader.Kind] of this [Shader].
 *
 * @since 0.0.1-alpha.4
 * @see Shader.formFactor
 * @see Shader.language
 * @see Shader.isOfKind
 * @see Shader.Kind.accepts
 */
public val Shader.kind: Shader.Kind<*, *> get() = Shader.Kind<Shader.Language, Shader.FormFactor>(language, formFactor)

/**
 * All [Shader.Kind]s this [Shader.Transpiler] can output.
 *
 * @since 0.0.1-alpha.4
 * @see Shader.isOfAnyKindOutputtedBy
 * @see Shader.Transpiler.Dynamic.outputKinds
 * @see Shader.Transpiler.Simple.outputKind
 */
public val <L, F> Shader.Transpiler<L, F>.outputKinds: ImmutableSet<Shader.Kind<L, F>> where L : Shader.Language, F : Shader.FormFactor
	get() = when (this) {
		is Shader.Transpiler.Dynamic -> outputKinds
		is Shader.Transpiler.Simple -> persistentHashSetOf(outputKind)
	}

/**
 * @author Laxystem
 */
private inline fun Shader.isAnyKindOutputtedBy(
	transpiler: Shader.Transpiler<*, *>,
	predicate: Shader.(Shader.Kind<*, *>) -> Boolean
) = when (transpiler) {
	is Shader.Transpiler.Dynamic -> transpiler.outputKinds.any { predicate(it) }
	is Shader.Transpiler.Simple -> predicate(transpiler.outputKind)
}

/**
 * Does this [Shader] belong to any [Shader.Kind] that the given [transpiler] can output?
 *
 * This function is faster than checking against [Shader.Transpiler.outputKinds],
 * as it does not heap-allocate unnecessarily.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun Shader.isOfAnyKindOutputtedBy(transpiler: Shader.Transpiler<*, *>): Boolean =
	isAnyKindOutputtedBy(transpiler) { this isOfKind it }

/**
 * Does this [Shader] belong to any [Shader.Kind] that the given [transpiler] can output?
 *
 * This function is faster than checking against [Shader.Transpiler.outputKinds],
 * as it does not heap-allocate unnecessarily.
 *
 * @since 0.0.1-alpha.4
 */
public infix fun Shader.isAcceptedByAnyKindOutputtedBy(transpiler: Shader.Transpiler<*, *>): Boolean =
	isAnyKindOutputtedBy(transpiler) { it accepts this }

/**
 * Transpiles this [shader], logging failures.
 *
 * @return `null` if the outputted shader does not [carry required metadata][isCarryingRequiredMetadata].
 */
public suspend fun <L, F> Shader.Transpiler<L, F>.transpile(shader: Shader): Shader.Transpiled<L, F>? where L : Shader.Language, F : Shader.FormFactor =
	runCatching {
		invoke(input = shader)
	}.let { result ->
		val output = result.getOrNull()

		when {
			result.isFailure -> logger.warn(result.exceptionOrNull()) { shader.transpilationTraceToString() + ": $this failed transpiling this shader" }
			output == null -> logger.debug(result.exceptionOrNull()) { shader.transpilationTraceToString() + ": $this refused transpiling this shader" }
			!(output.language accepts output) -> logger.warn(result.exceptionOrNull()) { output.transpilationTraceToString() + ": transpilation result missing metadata required by language" }
			!(output.formFactor accepts output) -> logger.warn(result.exceptionOrNull()) { output.transpilationTraceToString() + ": transpilation result missing metadata required by form factor" }
			else -> return output
		}

		return null
	}

/**
 * @author Laxystem
 */
@ExperimentalSpockApi
private data class WgslVertexShader<Vertex : Any>(
	override val vertexKind: VertexKind<Vertex>,
	override val code: String,
	override val entrypoint: String,
	override val label: String?,
	override val constants: ImmutableMap<String, Double>
) : VertexShader<Vertex>, Wgsl.Shader, StringShader

/**
 * Creates a [Wgsl]&nbsp;[VertexShader] from a [String].
 *
 * @param wgsl the [StringShader.code] of this shader.
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi
public fun <Vertex : Any> wgslVertexShaderOf(
	wgsl: String,
	vertexKind: VertexKind<Vertex>,
	entrypoint: String,
	label: String? = null,
	vararg constants: Pair<String, Double>
): VertexShader<Vertex> = WgslVertexShader(vertexKind, wgsl, entrypoint, label, persistentMapOf(*constants))

/**
 * Creates a [Wgsl]&nbsp;[VertexShader] from a [String].
 *
 * @param wgsl the [StringShader.code] of this shader.
 * @since 0.0.1-alpha.4
 */
@ExperimentalSpockApi
public fun <Vertex : Any> wgslVertexShaderOf(
	wgsl: String,
	vertexKind: VertexKind<Vertex>,
	entrypoint: String,
	label: String? = null,
	constants: Map<String, Double>
): VertexShader<Vertex> = WgslVertexShader(vertexKind, wgsl, entrypoint, label, constants.toImmutableMap())

/**
 * Caches transpilation results, filters unsupported shaders and
 * fallbacks to the [original][Shader.Transpiled.original] for [transpiled][Shader.Transpiled] shaders.
 *
 * Similarly to a [EverlastingCache], transpiled shaders are not forgotten unless manually [remove][Cache.forget]d.
 *
 * @since 0.0.1-alpha.4
 */
public fun ShaderCache(transpiler: Shader.Transpiler<*, *>): Cache<Shader, Shader?> =
	object : Cache<Shader, Shader?> {
		private val transpiledShaders = EverlastingCache(
			producer = transpiler::transpile,
			entryKind = Cache.Entry<Shader>::Reference
		)

		/**
		 * @author Laxystem
		 */
		private inline fun findAccepted(shader: Shader, findAccepted: (Shader) -> Shader?) = when {
			shader isAcceptedByAnyKindOutputtedBy transpiler -> shader
			shader is Shader.Transpiled<*, *> -> findAccepted(shader.original)
			else -> null
		}

		private fun findAccepted(shader: Shader): Shader? = findAccepted(shader, ::findAccepted)

		@RawSpockApi
		override suspend fun getEntry(shader: Shader): Cache.Entry<Shader?> =
			findAccepted(shader) { get(it) }?.let(Cache.Entry<Shader>::Reference) ?: transpiledShaders.getEntry(shader)

		override suspend fun getAndRemove(shader: Shader): Cache.Entry<Shader?> =
			findAccepted(shader) { getAndRemove(it).product }?.let(Cache.Entry<Shader>::Reference)
				?: transpiledShaders.getAndRemove(shader)

		override suspend fun contains(shader: Shader): Boolean =
			shader in transpiledShaders || shader is Shader.Transpiled<*, *> && shader.original in this

		override suspend fun forget(descriptor: Shader): Unit? {
			var result: Unit? = null
			if (descriptor is Shader.Transpiled<*, *>) result = forget(descriptor.original)
			result = transpiledShaders.forget(descriptor) ?: result
			return result
		}

		@ExperimentalSpockApi
		override suspend fun cacheAll(shaders: Sequence<Shader>) {
			transpiledShaders.cacheAll(shaders.mapNotNull(::findAccepted))
		}

		override suspend fun close() = transpiledShaders.close()
	}
