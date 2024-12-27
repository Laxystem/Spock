package quest.laxla.spock

internal expect val OperatingSystem.Companion.current: OperatingSystem

private val currentTarget = KTarget.Native(OperatingSystem.current)
public actual val KTarget.Companion.current: KTarget get() = currentTarget
