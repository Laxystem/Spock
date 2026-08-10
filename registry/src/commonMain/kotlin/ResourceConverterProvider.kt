package quest.laxla.spock.registry

import kotlinx.collections.immutable.ImmutableSet
import quest.laxla.spock.SuspendCloseable

public sealed interface ResourceConverter<T : Any, R : Any> : SuspendCloseable {
	public interface Sequential<T : Any, R : Any> : ResourceConverter<T, R> {
		public suspend fun T.convert(): R?
	}

	public interface Concurrent<T : Any, R : Any> : ResourceConverter<T, R> {
		public suspend fun T.convert(): R?
	}

	public interface Batched<T : Any, R : Any> : ResourceConverter<T, R> {
		public suspend fun List<T>.convert(): List<R?>
	}

	public interface Provider {
		public fun supportedOutputsFor(resourceType: ResourceType<*>): ImmutableSet<ResourceType<*>>

		/**
		 * @throws IllegalArgumentException unsupported conversion.
		 * @since 0.0.1-alpha.4
		 */
		public fun <T : Any, R : Any> acquireConverter(
			input: ResourceType<T>,
			output: ResourceType<R>
		): ResourceConverter<T, R>
	}
}
