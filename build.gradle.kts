plugins {
	alias(libs.plugins.publish) apply false
	base
}


@Suppress("PropertyName")
val GROUP: String by properties
private val _version = properties["version"]!!.toString()

allprojects {
	group = GROUP
	version = _version
}
