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

if (hasProperty("buildScan")) extensions.findByName("buildScan")?.withGroovyBuilder {
	setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
	setProperty("termsOfServiceAgree", "yes")
}
