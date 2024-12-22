package quest.laxla.spock

public sealed interface OperatingSystem {
	public sealed interface SupportedByAnyJvmBased : OperatingSystem
	public sealed interface JvmSupported : SupportedByAnyJvmBased
	
	public object Android : SupportedByAnyJvmBased
	public object FreeBsd : JvmSupported
	public object Linux : JvmSupported
	public object MacOS : JvmSupported
	public object Windows : JvmSupported
	public object ApplePhoneOS
	
	public companion object
}
