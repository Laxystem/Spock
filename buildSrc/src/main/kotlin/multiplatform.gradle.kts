plugins {
	kotlin("multiplatform")
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

	targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>() {
		binaries.all {
			// TODO: remove this once Kotlin updates to glibc 2.34+
			linkerOpts("-Wl,--allow-shlib-undefined")
		}
	}

	@OptIn(org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation::class)
	abiValidation {
		enabled = true
	}
}

tasks.build {
	dependsOn(tasks.checkLegacyAbi)
}
