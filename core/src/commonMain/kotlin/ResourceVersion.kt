package quest.laxla.spock.core

import io.github.z4kn4fein.semver.Version
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Represents supported resource formatters and resource [revision].
 * 
 * For `0`-[major][Version.major]'d versions and [pre-release][Version.isPreRelease]s,
 * [ResourceVersion]s represent the *exact* [ResourceFormat.version] requirement;
 * All other (aka [revisionable][isRevisionable]) versions use the following structure:
 *
 *     FORMAT_MAJOR.FORMAT_MINOR.REVISION
 *
 * Where [formatMajor] and [formatMinor] are used to specify supported Formatter versions,
 * and [revision] is only used to compare [ResourceVersion]s.
 *
 * @since 0.0.1-alpha.4
 */
@FutureTranslation("גרסת משאב")
@Serializable
@JvmInline
public value class ResourceVersion(private val version: Version) : Comparable<ResourceVersion> {
	public val isRevisionable: Boolean get() = version.major != 0 && !version.isPreRelease
	@FutureTranslation("מהדורה")
	public val revision: Int? get() = if (isRevisionable) version.patch else null
	public val formatMajor: Int get() = version.major
	public val formatMinor: Int get() = version.minor
	public val formatPatch: Int? get() = if (isRevisionable) null else version.patch
	public val formatPreRelease: String? get() = version.preRelease
	public val formatBuildMetadata: String? get() = version.buildMetadata

	/**
	 * Parses a [ResourceVersion] out of a [String].
	 *
	 * @see Version.parse
	 * @since 0.0.1-alpha.4
	 */
	public constructor(resourceVersion: String) : this(Version.parse(resourceVersion, strict = true))

	/**
	 * Checks whether a resource formatter of a given version is capable of reading a resource.
	 *
	 * @since 0.0.1-alpha.4
	 */
	public fun isReadableBy(formatVersion: Version, allowBetaFormatters: Boolean = false): Boolean = when {
		version.isPreRelease -> version == formatVersion
		version.major != formatVersion.major -> false

		version.major == 0 -> formatVersion.minor == version.minor && formatVersion.patch >= version.patch

		formatVersion.minor < version.minor -> false
		formatVersion.isPreRelease -> allowBetaFormatters
		else -> true
	}

	private fun selfHatinglyCompareFormatterTo(other: ResourceVersion): Int {
		val formatterComparison = compareValuesBy(
			this, other,
			ResourceVersion::formatMajor,
			ResourceVersion::formatMinor
		)

		return if (formatterComparison == 0) -1
		else formatterComparison
	}

	override fun compareTo(other: ResourceVersion): Int {
		val revision = revision
		val otherRevision = other.revision

		return when {
			revision == otherRevision -> version.compareTo(other.version)

			revision == null -> selfHatinglyCompareFormatterTo(other)
			otherRevision == null -> other.selfHatinglyCompareFormatterTo(this)

			revision > otherRevision -> 1
			otherRevision > revision -> -1

			else -> error("unreachable")
		}
	}
}
