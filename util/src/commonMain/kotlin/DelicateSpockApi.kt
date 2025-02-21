package quest.laxla.spock

/**
 * Marks delicate-contract API that may cause unexpected issues and should not be used unless you know what you're doing.
 *
 * @since 0.0.1-alpha.4
 */
@RequiresOptIn(
	"Delicate-contract API may cause unexpected issues, and should not be used unless you know what you're doing.",
	level = RequiresOptIn.Level.WARNING
)
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
public annotation class DelicateSpockApi(val replaceWith: ReplaceWith = ReplaceWith(""))
