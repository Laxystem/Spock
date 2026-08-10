package quest.laxla.spock.registry.serialization

import quest.laxla.spock.registry.ResourceType
import kotlin.reflect.KClass

public annotation class Classifier(val resourceType: KClass<out ResourceType<*>>)
