package quest.laxla.spock

@Deprecated(
	message = "Superseded by a Kotlin built-in",
	replaceWith = ReplaceWith(expression = "contextOf<T>()")
)
context(context: T)
public fun <T> implicit(): T = context
