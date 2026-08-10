plugins {
	alias(libs.plugins.kotlinx.atomicfu)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.publish)
	dokka
	multiplatform
	powerAssert
}

dependencies {
	commonMainApi(projects.core)
}

kotlin {
	jvm()
	linuxArm64()
	linuxX64()
	mingwX64()
	wasmJs().browser()
}
