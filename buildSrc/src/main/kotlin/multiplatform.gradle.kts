import kotlinx.validation.ExperimentalBCVApi

plugins {
	kotlin("multiplatform")
	id("org.jetbrains.kotlinx.binary-compatibility-validator")
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	compilerOptions {
		allWarningsAsErrors = true
		freeCompilerArgs.add("-Xexpect-actual-classes")
	}

	withSourcesJar(publish = true)
}

apiValidation {
	@OptIn(ExperimentalBCVApi::class)
	klib {
		enabled = true
	}
}
