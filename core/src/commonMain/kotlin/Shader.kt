package quest.laxla.spock

import kotlinx.collections.immutable.ImmutableSet

/**
 * Represents a program to be run on the GPU.
 *
 * Shaders are provided to [Renderer]s via [Pipeline]s, and are defined by two parameters;
 * * [Kind], that itself consists of two more parameters;
 *    * [Language] (e.g. [Wgsl]), accessible via [Shader.language].
 *    * [FormFactor] (e.g. [StringShader.FormFactor]), accessible via [Shader.formFactor].
 * * Type represented by sub-interfaces that are referred to directly by [Pipeline]s (e.g. [VertexShader]).
 *
 * Shading languages and form factors often require shaders
 * to implement an additional interface carrying additional metadata (e.g. [Wgsl.Shader.entrypoint]),
 * or in the case of form factors, the code itself (e.g. [StringShader.code]).
 *
 * [Renderer]s are expected to deduplicate shaders that have same code and [label].
 *
 * @since 0.0.1-alpha.4
 */
public interface Shader {
	/**
	 * The language this [Shader] is written in.
	 *
	 * @since 0.0.1-alpha.4
	 * @see Shader.kind
	 */
	public val language: Language

	/**
	 * The [FormFactor] of this [Shader].
	 *
	 * @since 0.0.1-alpha.4
	 * @see Shader.kind
	 */
	public val formFactor: FormFactor

	/**
	 * The label this [Shader] should be referred to as for logging purposes.
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
	 * Represents a shader form, e.g., a [StringShader].
	 *
	 * Implementations **must** properly implement [equals], [hashCode], and [toString];
	 * Therefore, it is recommended to use `data object`s.
	 *
	 * @since 0.0.1-alpha.4
	 * @sample StringShader
	 */
	public interface FormFactor {
		/**
		 * Does this [shader] carry all metadata required by this form factor?
		 *
		 * This function always returns `false` if the shader's [FormFactor] is not this one.
		 *
		 * @since 0.0.1-alpha.4
		 * @see Shader.isCarryingRequiredMetadata
		 */
		public infix fun accepts(shader: Shader): Boolean
	}

	/**
	 * Combination of a [FormFactor] and a [Language].
	 *
	 * @property formFactor the form factor of [Shader]s of this kind.
	 * @property language the language [Shader]s of this kind are written in.
	 * @since 0.0.1-alpha.4
	 * @see Shader.kind
	 */
	public data class Kind<out L, out F>(
		val language: Language,
		val formFactor: FormFactor
	) where L : Language, F : FormFactor

	/**
	 * Implemented by [Shader]s dynamically compiled from another shader of a different [Language] or [FormFactor].
	 *
	 * @param L the language this shader was transpiled to.
	 * @param F the form factor this shader was transpiled to.
	 * @since 0.0.1-alpha.4
	 * @see Transpiler
	 */
	public interface Transpiled<out L, out F> : Shader where L : Language, F : FormFactor {
		/**
		 * The original [Shader] this one was compiled from.
		 *
		 * @since 0.0.1-alpha.4
		 */
		public val original: Shader

		override val language: L
		override val formFactor: F
	}

	/**
	 * Transpiles [Shader]s of one [Kind] to another.
	 *
	 * @since 0.0.1-alpha.4
	 * @see Transpiled
	 */
	public sealed interface Transpiler<out L, out F> where L : Language, F : FormFactor {
		/**
		 * Transpiles the [input]ted [Shader] to one of the [outputKinds].
		 *
		 * It is expected but not required that the output [carries all required metadata][Shader.isCarryingRequiredMetadata].
		 *
		 * @return `null` if this [Transpiler] does not support the provided [input].
		 * @since 0.0.1-alpha.4
		 * @see transpile
		 */
		public suspend operator fun invoke(input: Shader): Transpiled<L, F>?

		/**
		 * Transpiles [Shader]s to a single, constant [outputKind].
		 *
		 * @since 0.0.1-alpha.4
		 * @see Transpiler
		 * @see Transpiler.Dynamic
		 */
		public interface SingleTarget<out L, out F> : Transpiler<L, F> where L : Language, F : FormFactor {
			/**
			 * The [Kind] of all [Shader]s outputted by this [Transpiler].
			 *
			 * @since 0.0.1-alpha.4
			 */
			public val outputKind: Kind<L, F>
		}

		/**
		 * Transpile [Shader]s to any of the pre-selected [outputKinds].
		 *
		 * It is up to the implementation to select the optimal [Kind].
		 *
		 * @see 0.0.1-alpha.4
		 */
		public interface Dynamic<out L, out F> : Transpiler<L, F> where L : Language, F : FormFactor {
			/**
			 * All [Shader.Kind]s this [Transpiler] can output.
			 *
			 * @since 0.0.1-alpha.4
			 */
			public val outputKinds: ImmutableSet<Kind<L, F>>
		}
	}
}
