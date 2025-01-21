package quest.laxla.spock

/**
 * Any kind of shader.
 *
 * @since 0.0.1-alpha.4
 */
public interface Shader {
	/**
	 * The language this [Shader] is written in.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val language: Language

	/**
	 * The label the graphics API should refer to this [Shader] under for logging purposes.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val label: String? get() = null

	/**
	 * Represents a shading language, such as [WGSL][Wgsl] or GLSL.
	 *
	 * Implementations **must** properly implement [equals], [hashCode] and [toString];
	 * Therefore, it is recommended to use `data object`s.
	 *
	 * @since 0.0.1-alpha.4
	 * @sample Wgsl
	 */
	public interface Language {
		/**
		 * Does this [shader] carry all metadata required by this shading language?
		 *
		 * This function always returns `false` if the shader's [language] is not this one.
		 *
		 * @since 0.0.1-alpha.4
		 * @see Shader.isCarryingRequiredMetadata
		 */
		public infix fun accepts(shader: Shader): Boolean
	}

	/**
	 * Implemented by [Shader]s dynamically compiled from another shader of a different [Language] or form factor.
	 *
	 * @param L the language this shader was transpiled to.
	 * @since 0.0.1-alpha.4
	 * @see Transpiler
	 */
	public interface Transpiled<L> : Shader where L : Language {
		/**
		 * The original [Shader] this one was compiled from.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val original: Shader

		override val language: L
	}

	/**
	 * Transpiles [Shader] from one [Language] or form factor to another if not supported by the [Renderer].
	 *
	 * @since 0.0.1-alpha.4
	 * @see Transpiled
	 */
	public interface Transpiler<L> where L : Language {
		/**
		 * The [Language] the shaders this transpiler outputs are written in.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val outputLanguage: L

		/**
		 * Transpiles the [input] [Shader] to the [outputLanguage].
		 *
		 * It is expected but not required that the output is [accept][Language.accepts]ed;
		 * However, prefer returning an unaccepted shader over `null` when possible and not expensive.
		 *
		 * @return `null` if this [Transpiler] does not support this [input].
		 * @since 0.0.1-alpha.4
		 */
		public suspend operator fun invoke(input: Shader): Transpiled<L>?
	}
}
