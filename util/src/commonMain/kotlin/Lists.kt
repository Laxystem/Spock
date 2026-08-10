package quest.laxla.spock

import kotlinx.collections.immutable.PersistentList

/**
 * Returns a new persistent list with the last element removed.
 *
 * @throws NoSuchElementException if the list is empty.
 * @since 0.0.1-alpha.4
 * @see removeLastOrNull
 */
public fun <E> PersistentList<E>.removingLast(): PersistentList<E> =
	if (isEmpty()) throw NoSuchElementException("Cannot remove last element of an empty list.")
	else removingAt(lastIndex)


/**
 * Returns a new persistent list with the last element removed, or the same list if it is empty.
 *
 * @since 0.0.1-alpha.4
 * @see removeLastOrNull
 */
public fun <E> PersistentList<E>.removingLastIfNotEmpty(): PersistentList<E> =
	if (isEmpty()) this
	else removingAt(lastIndex)
