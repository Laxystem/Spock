package quest.laxla.spock

/**
 * @author Laxystem
 */
private data object EmptyIterator : Iterator<Nothing> {
	override fun next(): Nothing = throw NoSuchElementException("This iterator is always empty.")

	override fun hasNext(): Boolean = false
}

/**
 * @since 0.0.1-alpha.4
 */
public fun <T> emptyIterator(): Iterator<T> = EmptyIterator
