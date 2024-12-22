@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	`maven-publish`
	dokka
	multiplatform
	signing
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())

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
