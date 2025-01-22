@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

repositories.maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
	name = "Maven Central Snapshots"
	mavenContent { includeGroup("com.varabyte.truthish") }
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
			api(libs.kotlinx.immutableCollections)
			api(libs.kotlinx.io.bytestring)
			api(libs.kotlinx.serialization)
		}

		commonTest.dependencies {
			api(kotlin("test"))
			api(libs.truthish)
		}

		jvmMain.dependencies {
			runtimeOnly(libs.logback)
		}

		wasmJsMain.dependencies {
			api(libs.kotlinx.browser)
		}
	}
}
