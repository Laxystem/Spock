@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	dokka
	multiplatform
	alias(libs.plugins.publish)
}

kotlin {
	jvm()
	linuxX64()
	wasmJs().browser()

	sourceSets {
		commonMain.dependencies {
			api(libs.kotlinx.coroutines)
			api(libs.kotlinLogging)
		}

		commonTest.dependencies {
			api(kotlin("test"))
		}
		
		jvmMain.dependencies {
			runtimeOnly(libs.logback)
		}
		
		wasmJsMain.dependencies {
			api(libs.kotlinx.browser)
		}
	}
}
