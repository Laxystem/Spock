package quest.laxla.spock.registry

/**
 * Constructs resources of type [T] using a [Registry] lookup.
 *
 * It is recommended, but not required, for the constructed resource to be [quest.laxla.spock.core.Named];
 * If not, it'll be automatically named using a [quest.laxla.spock.core.Namer] by the calling context.
 *
 * @since 0.0.1-alpha.4
 */
public fun interface ResourceConstructor<out T : Any> {
	context(lookup: Registry)
	public suspend fun construct(): T
}