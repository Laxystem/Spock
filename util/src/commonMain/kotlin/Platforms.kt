package quest.laxla.spock

/**
 * The current Kotlin target.
 * 
 * Note, Wasm targets currently don't support inferring the current [operating system][Target.operatingSystem].
 * 
 * @since 0.0.1-alpha.1
 */
@ExperimentalSpockApi
public expect val Target.Companion.current: Target
