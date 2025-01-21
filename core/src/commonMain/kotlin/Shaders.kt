package quest.laxla.spock

/**
 * Does this [Shader] carry all metadata required by the [language][Shader.language] it is written in?
 *
 * @since 0.0.1-alpha.4
 * @see Shader.Language.accepts
 */
public val Shader.isCarryingRequiredMetadata: Boolean get() = language accepts this

/**
 * @author Laxystem
 */
private data class WgslVertexShader<Vertex : Any>(
	override val vertexKind: VertexKind<Vertex>,
	override val code: String,
	override val entrypoint: String,
	override val label: String?
) : VertexShader<Vertex>, Wgsl.Shader, StringShader

/**
 * Creates a [Wgsl]&nbsp;[VertexShader] from a [String].
 *
 * @since 0.0.1-alpha.4
 */
public fun <Vertex : Any> wgslVertexShaderOf(
	wgsl: String,
	vertexKind: VertexKind<Vertex>,
	entrypoint: String,
	label: String? = null
): VertexShader<Vertex> = WgslVertexShader(vertexKind, wgsl, entrypoint, label)
