@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinx.serialization)
	dokka
	multiplatform
}

dependencies {
	commonMainApi(projects.util)
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
}
