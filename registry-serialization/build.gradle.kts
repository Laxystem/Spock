plugins {
	multiplatform
	alias(libs.plugins.kotlinx.serialization)
}

dependencies {
	commonMainApi(projects.registry)
}

kotlin {
	jvm()
}
