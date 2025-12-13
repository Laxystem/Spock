package quest.laxla.spock

import kotlinx.coroutines.flow.Flow

/**
 * @since 0.0.1-alpha.4
 */
public fun interface ResourceSource<out T> where T : Any {
	/**
	 * @since 0.0.1-alpha.4
	 */
	public fun list(resourceName: Name): Flow<RawResource<T>>
}
