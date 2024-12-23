plugins {
	kotlin("multiplatform")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	
	withSourcesJar(publish = true)
}
