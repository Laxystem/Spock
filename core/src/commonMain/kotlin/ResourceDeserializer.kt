package quest.laxla.spock

/**
 * Deserializes [RawResource]s into the final [Resource].
 *
 * @since 0.0.1-alpha.4
 * @see ResourceDeserializationMethod
 */
public fun interface ResourceDeserializer<in T, R> where T : Any, R : Any {
	/**
	 * Deserializes this [resource] into its final, usable form.
	 *
	 * @return `null` if deserialization failed or unsupported.
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun deserialize(resource: RawResource<T>): Resource<R>?
}
