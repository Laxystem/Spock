plugins {
	kotlin("multiplatform")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	compilerOptions {
		freeCompilerArgs.add("-Xexpect-actual-classes")
	}

	withSourcesJar(publish = true)
}
