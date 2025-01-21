package quest.laxla.spock.math

import kotlin.reflect.KClass

/**
 * Generates serializers for [Vector]s of the annotated [Space].
 * 
 * @property name The names of the generated typealiases and their [serializer][kotlinx.serialization.KSerializer]s,
 * with `#` standing for the [Vector]'s element count.
 * @property type The `V` of the annotated [Space].
 * 
 * @since 0.0.1-alpha.4
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
public annotation class Typealiased(val name: String, val type: KClass<*>)
