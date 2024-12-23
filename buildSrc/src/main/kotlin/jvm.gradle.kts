plugins {
	kotlin("jvm")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	
	target.withSourcesJar(publish = true)
}
