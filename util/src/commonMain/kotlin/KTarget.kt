package quest.laxla.spock

/**
 * Kotlin/Multiplatform compilation target.
 * 
 * @since 0.0.1-alpha.1
 */
public sealed interface KTarget {
	/**
	 * The current operating system if known.
	 * 
	 * @since 0.0.1-alpha.1
	 */
	public val operatingSystem: OperatingSystem?

	/**
	 * Compiles to WebAssembly and uses web APIs.
	 * 
	 * @since 0.0.1-alpha.1
	 */
	public data class WasmJs(override val operatingSystem: OperatingSystem? = null) : KTarget

	/**
	 * Compiles to WebAssembly and uses the WASI API.
	 * 
	 * @since 0.0.1-alpha.4
	 */
	public data class WasmWasi(override val operatingSystem: OperatingSystem? = null) : KTarget

	/**
	 * @since 0.0.1-alpha.1
	 * @see OperatingSystem.SupportedByAnyJvmBased
	 */
	public sealed interface JvmBased : KTarget {
		override val operatingSystem: OperatingSystem.SupportedByAnyJvmBased?
	}

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data class Jvm(override val operatingSystem: OperatingSystem.JvmSupported? = null) : JvmBased

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data object Android : JvmBased {
		override val operatingSystem: OperatingSystem.Android get() = OperatingSystem.Android
	}

	/**
	 * @since 0.0.1-alpha.1
	 */
	public data class Native(override val operatingSystem: OperatingSystem? = null) : KTarget

	/**
	 * @since 0.0.1-alpha.1
	 */
	public companion object
}
