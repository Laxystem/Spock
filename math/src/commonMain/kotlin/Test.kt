package quest.laxla.spock.math

public fun test() {
	val vector = IntSpace.vectorOf(1, 2, 3, 4)
	IntSpace x 1 yz vector.wx w 4
	IntSpace x 1 y 2 z 3 w 4

	val vector4 = (IntSpace x 5 y 21) + IntSpace.vectorOf(1, 6) / 3
	
	-vector4 mod 2

	IntSpace.uniformVectorOf(64).asVector2()
}
