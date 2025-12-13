package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Represents a [Resource] that still needs to go through some process to be usable.
 *
 * @since 0.0.1-alpha.4
 */
public interface RawResource<out T> where T : Any {
	/**
	 * The [Name]s of the [ResourceDeserializationMethod]s this resource needs go through, by order.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val deserializationMethod: ImmutableList<Name> get() = persistentListOf()

	/**
	 * Acquire the resource in its serialized form.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public suspend fun acquire(): T

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {
		private data class RawRawResource<out T : Any>(
			val value: T,
			override val deserializationMethod: ImmutableList<Name> = persistentListOf()
		) : RawResource<T> {
			override suspend fun acquire(): T = value

			override fun toString(): String = "RawResource($value,deserializationMethod=$deserializationMethod)"
		}

		/**
		 * Creates a [RawResource] using a known [value] for [RawResource.acquire].
		 *
		 * @since 0.0.1-alpha.4
		 */
		public operator fun <T> invoke(
			value: T,
			deserializationMethod: ImmutableList<Name> = persistentListOf()
		): RawResource<T> where T : Any = RawRawResource(value, deserializationMethod)
	}
}
