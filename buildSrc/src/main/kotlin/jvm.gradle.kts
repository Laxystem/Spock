plugins {
	kotlin("jvm")
	id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	compilerOptions.allWarningsAsErrors = true

	target.withSourcesJar(publish = true)
}
