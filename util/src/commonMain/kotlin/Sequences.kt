package quest.laxla.spock

/**
 * Removes the first entry of this sequence, if there is one.
 *
 * @since 0.0.1-alpha.4
 */
public fun <T> Sequence<T>.skipFirst(): Sequence<T> = object : Sequence<T> {
	override fun iterator(): Iterator<T> = object : Iterator<T> {
		val iterator = this@skipFirst.iterator()

		init {
			if (iterator.hasNext()) next()
		}

		override fun next(): T = iterator.next()

		override fun hasNext(): Boolean = iterator.hasNext()
	}
}
