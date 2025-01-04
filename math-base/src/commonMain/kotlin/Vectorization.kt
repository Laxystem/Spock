package quest.laxla.spock.math

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.VALUE_PARAMETER)
public annotation class Vectorization(val kind: Kind) {
	public enum class Kind {
		/**
		 * [Vector1], [Vector2], [Vector3], [Vector4], etc...
		 */
		Exact,

		/**
		 * [VectorWithX], [VectorWithY], [VectorWithZ], [VectorWithW], etc...
		 */
		Minimum,
		
		Disabled
	}
}
