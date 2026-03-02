package quest.laxla.spock

/*
/**
 * Bitshift class over [UInt] that may be used by [Ecs] implementations to implement their opaque entity IDs.
 *
 * @since 0.0.1-alpha.4
 */
@JvmInline
@Serializable
public value class VersionedEntityID(private val raw: UInt) {
	/**
	 * The part identifying the entity referred to by the ID.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val ordinal: UInt get() = raw and ORDINAL_MASK

	/**
	 * The entity version. Increased for an ordinal whenever the entity it is referring to is deleted,
	 * thus allowing another to take its place without fearing running out of IDs.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public val version: UInt get() = raw and VERSION_MASK

	/**
	 * @since 0.0.1-alpha.4
	 */
	public constructor(
		ordinal: UInt = 0u,
		version: UInt = 0u
	) : this(raw = version shl ORDINAL_BITS or (ordinal shl ORDINAL_BITS shr ORDINAL_BITS))

	/**
	 * Returns another [quest.laxla.spock.VersionedEntityID] with the same [ordinal] and a [version] increased by one.
	 *
	 * @since 0.0.1-alpha.4
	 * @see nextOrdinal
	 */
	public fun nextVersion(): VersionedEntityID = VersionedEntityID(raw + (1u shl ORDINAL_BITS))

	/**
	 * Returns another [quest.laxla.spock.VersionedEntityID] with the same [version] and an [ordinal] increased by one.
	 *
	 * @since 0.0.1-alpha.4
	 * @see nextVersion
	 */
	public fun nextOrdinal(): VersionedEntityID = VersionedEntityID(raw = version or (ordinal + 1u))

	/**
	 * Returns the [UInt] representation of this [quest.laxla.spock.VersionedEntityID].
	 *
	 * @see 0.0.1-alpha.4
	 */
	public fun toUInt(): UInt = raw

	override fun toString(): String = ordinal.toHexString(HexFormat.UpperCase) + "v" + version.toHexString()

	/**
	 * @since 0.0.1-alpha.4
	 */
	public companion object {
		private const val ORDINAL_BITS = 16

		// TODO: make const
		private val ORDINAL_MASK = UInt.MAX_VALUE shr ORDINAL_BITS
		private val VERSION_MASK = UInt.MAX_VALUE shl ORDINAL_BITS

	}
}

 */