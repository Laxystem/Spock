package quest.laxla.spock.registry.serialization

@MustBeDocumented
@RequiresOptIn(message = "Noop serializers should not be used directly. Use @Classifier instead.")
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
public annotation class InternalNoopSerializer
