package quest.laxla.spock

public sealed interface Target {
	public val operatingSystem: OperatingSystem?

	public data class WasmJs(override val operatingSystem: OperatingSystem? = null) : Target

	public sealed interface JvmBased : Target {
		override val operatingSystem: OperatingSystem.SupportedByAnyJvmBased?
	}

	public data class Jvm(override val operatingSystem: OperatingSystem.JvmSupported? = null) : JvmBased
	
	public object Android : JvmBased {
		override val operatingSystem: OperatingSystem.Android get() = OperatingSystem.Android
	}
	
	public data class Native(override val operatingSystem: OperatingSystem? = null) : Target
	
	public companion object
}
