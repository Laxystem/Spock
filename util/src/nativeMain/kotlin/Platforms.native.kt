package quest.laxla.spock

internal expect val OperatingSystem.Companion.current: OperatingSystem

private val currentTarget = Target.Native(OperatingSystem.current)
public actual val Target.Companion.current: Target get() = currentTarget
