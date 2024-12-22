plugins {
	kotlin("multiplatform")
}

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
}
