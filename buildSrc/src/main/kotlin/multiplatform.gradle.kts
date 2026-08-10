import org.gradle.kotlin.dsl.registering
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.konan.target.Family

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
		if (konanTarget.family == Family.LINUX) binaries.all {
			// TODO: remove this once Kotlin updates to glibc 2.34+
			linkerOpts("-Wl,--allow-shlib-undefined")
		}
	}

	@OptIn(ExperimentalAbiValidation::class)
	abiValidation()
}

//tasks {
//	build {
//		dependsOn(tasks.checkLegacyAbi)
//	}
//
//	checkLegacyAbi {
//		mustRunAfter(updateLegacyAbi)
//	}
//}
