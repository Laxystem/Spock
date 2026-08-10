package quest.laxla.spock.registry

import quest.laxla.spock.ConcurrentDataStructure
import quest.laxla.spock.core.ResourceMetadata
import quest.laxla.spock.registry.impl.immutableRegistry

public interface Registry : ConcurrentDataStructure<Registry, MutableRegistry, Registrar> {
	/**
	 * Acquires a [resource].
	 *
	 * @return `null` if it is not registered.
	 * @since 0.0.1-alpha.4
	 */
	public suspend operator fun <E : Any> get(resource: ResourceKey<E>): E?

	/**
	 * Acquires the [ResourceKey] the provided [resource] is registered under.
	 *
	 * @return `null` if it is not registered.
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun <T : E, E : Any> keyOfOrNull(classifier: ResourceClassifier<E>, resource: T): Key<T, E>?

	public operator fun contains(resource: ResourceKey<*>): Boolean
	public operator fun contains(resource: AcquiredResource<*>): Boolean

	/**
	 * Tells the [Registry] the provided [resources] will likely be used soon.
	 *
	 * The registry may throw immediately if it does not contain and cannot acquire that resource,
	 * initiate a fetch operation, or simply do nothing; This is merely an optimization trick.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun request(resource: Resource<*>) {}

	/**
	 * @since 0.0.1-alpha.4
	 */
	public data class Entry<out T : Any>(
		val resource: T,
		val metadata: ResourceMetadata
	)

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {
		/**
		 * The empty-most registry possible.
		 * All [functional][isFunctional] registries must contain these items.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val Basic: Registry = immutableRegistry {
			register(ResourceType.Root)
		}
	}
}
