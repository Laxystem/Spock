@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
	alias(libs.plugins.kotlinSymbolProcessing)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

dependencies {
	commonMainApi(projects.mathBase)
	kspCommonMainMetadata(projects.mathCodegen)
}

kotlin {
	jvm()
	linuxX64()
	linuxArm64()
	mingwX64()
	wasmJs {
		browser()
		binaries.library()
	}
	
	sourceSets.commonMain {
		kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
	}
}

val dependency = "kspCommonMainKotlinMetadata"
tasks.withType<KotlinCompilationTask<*>>().all {
	if (name != dependency) dependsOn(dependency)
}

ksp {
	arg("package", "quest.laxla.spock.math")
}
