package quest.laxla.spock

public data class Resource<T : Any>(
	val warehouse: Warehouse,
	val name: Name
) {
}