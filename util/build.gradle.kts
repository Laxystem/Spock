@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
	powerAssert
}

kotlin {
	jvm()
	linuxArm64()
	linuxX64()
	mingwX64()
	wasmJs().browser()

	sourceSets {
		commonMain.dependencies {
			api(libs.kotlinLogging)
			api(libs.kotlinx.coroutines)
			api(libs.kotlinx.collections.immutable)
			api(libs.kotlinx.io.bytestring)
			api(libs.kotlinx.serialization)
		}

		commonTest.dependencies {
			api(kotlin("test"))
			api(libs.kotlinx.coroutines.test)
		}

		jvmMain.dependencies {
			runtimeOnly(libs.logback)
		}

		wasmJsMain.dependencies {
			api(libs.kotlinx.browser)
		}
	}
}
