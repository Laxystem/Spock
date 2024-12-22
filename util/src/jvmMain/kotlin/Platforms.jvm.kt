package quest.laxla.spock

private val currentTarget = System.getProperty("os.name").run {
	when {
		startsWith("SunOS", ignoreCase = true) || startsWith("Unit", ignoreCase = true) ||
				contains("Linux", ignoreCase = true) -> OperatingSystem.Linux
		startsWith("Win", ignoreCase = true) || contains("Windows", ignoreCase = true) -> OperatingSystem.Windows
		startsWith("Mac", ignoreCase = true) || contains("Darwin", ignoreCase = true) -> OperatingSystem.MacOS
		contains("BSD", ignoreCase = true) && contains("Free", ignoreCase = true) -> OperatingSystem.FreeBsd

		else -> null
	}
}.let(Target::Jvm)

public actual val Target.Companion.current: Target get() = currentTarget
