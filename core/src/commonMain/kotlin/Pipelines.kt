package quest.laxla.spock

private const val PipelineVertexKindDeprecationMessage = "Vertex kinds only affect the vertex shader, not the entire pipeline."

/**
 * @since 0.0.1-alpha.4
 */
@Suppress("UnusedReceiverParameter")
@Deprecated(
	message = PipelineVertexKindDeprecationMessage,
	level = DeprecationLevel.ERROR,
	replaceWith = ReplaceWith("this.vertexShader.vertexKind")
)
@ExperimentalSpockApi
public val <Vertex : Any> Pipeline<Vertex>.vertexKind: VertexKind<Vertex>
	get() = throw NotImplementedError(PipelineVertexKindDeprecationMessage)
