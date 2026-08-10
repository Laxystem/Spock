package quest.laxla.spock.registry

/**
 * Generates resources of type [T], with no need for a [Registry] lookup to do so.
 *
 * Generators may still override [construct] if they do wish to utilize a registry lookup if available;
 * Thus, it is recommended for consumers of this interface to call [construct] and not [generate] whenever possible.
 *
 * @since 0.0.1-alpha.4
 */
public fun interface ResourceGenerator<out T : Any> : ResourceConstructor<T> {
	public suspend fun generate(): T

	context(lookup: Registry)
	public override suspend fun construct(): T = generate()
}
