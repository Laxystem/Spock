package quest.laxla.spock.serialization.plugin.kotlin

import org.jetbrains.kotlin.name.ClassId
import quest.laxla.spock.registry.serialization.plugin.*

enum class SerializableReferenceClass(
	val classId: ClassId,
	val serializer: ClassId,
	val noopSerializer: ClassId,
	val acceptsImplSerializer: Boolean = true
) {
	Box(
		classId = boxName.toClassId(),
		serializer = boxSerializerName.toClassId(),
		noopSerializer = boxNoopSerializerName.toClassId()
	),
	Key(
		classId = keyName.toClassId(),
		serializer = keySerializerName.toClassId(),
		noopSerializer = keyNoopSerializerName.toClassId(),
		acceptsImplSerializer = false
	),
	Registration(
		classId = registrationName.toClassId(),
		serializer = registrationSerializerName.toClassId(),
		noopSerializer = registrationNoopSerializerName.toClassId()
	);

	companion object {
		operator fun get(classId: ClassId): SerializableReferenceClass? = try {
			entries.single { it.classId == classId }
		} catch (_: NoSuchElementException) {
			null
		}
	}
}
