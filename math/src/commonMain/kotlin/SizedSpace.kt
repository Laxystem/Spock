package quest.laxla.spock.math

public interface SizedSpace<V> : Space<V> {
	public val sizeInBytes: Int
	public val sizeInBits: Int
}
