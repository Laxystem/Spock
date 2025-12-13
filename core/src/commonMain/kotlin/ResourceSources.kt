package quest.laxla.spock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * @author Laxystem
 */
private data object Empty : ResourceSource<Nothing> {
	override fun list(resourceName: Name): Flow<Nothing> = emptyFlow()
}

/**
 * Returns an empty [ResourceSource].
 *
 * @since 0.0.1-alpha.4
 */
public fun <T> emptyResourceSource(): ResourceSource<T & Any> = Empty
