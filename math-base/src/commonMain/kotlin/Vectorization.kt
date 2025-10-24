package quest.laxla.spock.math

/**
 * Controls how annotated [VectorOperation] parameters will be expanded.
 *
 * If a parameter is not annotated,
 * the code generator will default to [Disabled][quest.laxla.spock.math.Vectorization.Kind.Disabled].
 * This default can be changed by annotating the type parameter itself.
 *
 * @since 0.0.1-alpha.4
 * @see quest.laxla.spock.math.Vectorization.Kind
 */
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.VALUE_PARAMETER)
public annotation class Vectorization(val kind: Kind) {
	/**
	 * Specifies the different ways in which a [VectorOperation] parameter may be expanded.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public enum class Kind {
		/**
		 * Parameters will be expanded to require vectors of *exactly* the same amount of elements,
		 * using [Vector1], [Vector2], [Vector3], [Vector4], etc...
		 *
		 * @since 0.0.1-alpha.4
		 */
		Exact,

		/**
		 * Parameters will be expanded to require vectors that have *at least* the relevant amount of elements,
		 * using [VectorWithX], [VectorWithY], [VectorWithZ], [VectorWithW], etc...
		 *
		 * @since 0.0.1-alpha.4
		 */
		Minimum,

		/**
		 * Parameters will not be expanded at all, rather instead will use the types written in code.
		 *
		 * @since 0.0.1-alpha.4
		 */
		Disabled
	}
}
