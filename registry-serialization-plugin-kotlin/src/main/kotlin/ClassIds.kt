package quest.laxla.spock.serialization.plugin.kotlin

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import quest.laxla.spock.registry.serialization.plugin.classifierAnnotationName

internal fun String.toClassId() = ClassId.topLevel(FqName(this))

val classifierAnnotation = classifierAnnotationName.toClassId()
val serializableAnnotation = "kotlinx.serialization.Serializable".toClassId()
