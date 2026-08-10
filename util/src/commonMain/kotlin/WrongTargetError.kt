package quest.laxla.spock

/**
 * @since 0.0.1-alpha.4
 */
public class WrongTargetError(
	public val intendedTarget: KTarget,
	message: String? = null,
	cause: Throwable? = null
) : AssertionError(
	"Expected target $intendedTarget, received ${KTarget.current}" + message.orEmpty().trimAndAppendIfNotBlank(prefix = ": "),
	cause
), AssertionStrength.Specified {
	override val assertionStrength: AssertionStrength
		get() = AssertionStrength.Assertion
}
