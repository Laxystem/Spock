package quest.laxla.spock

/**
 * Deserializes [RawResource]s of type [T] into medial or final representation [R].
 *
 * @since 0.0.1-alpha.4
 */
public fun interface ResourceDeserializationMethod<in T, R> where T : Any, R : Any {
    /**
     * Deserializes this raw [resource] into representation [R].
     *
     * @return `null` if deserialization is not supported, or a [Result] if it has succeeded or failed.
     * @since 0.0.1-alpha.4
     */
    public suspend fun deserialize(resource: T): Result<R>?
}