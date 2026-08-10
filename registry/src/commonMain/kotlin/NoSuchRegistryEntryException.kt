package quest.laxla.spock.registry

public class NoSuchRegistryEntryException(public val resource: Resource<*>) :
	NoSuchElementException(message = "No resource registered under key [$resource]")
