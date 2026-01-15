plugins {
	kotlin("jvm")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	compilerOptions.allWarningsAsErrors = true

	target.withSourcesJar(publish = true)
}
