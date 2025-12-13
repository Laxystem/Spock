package quest.laxla.spock

/**
 * Represents a resource that is ready to be used as-is.
 *
 * @since 0.0.1-alpha.4
 */
public data class Resource<T>(val value: T, val resourceType: ResourceType<in T>) : RawResource<T> where T : Any {
	override suspend fun acquire(): T = value
}
