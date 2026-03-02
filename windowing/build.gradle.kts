@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

kotlin {
	jvm()
	linuxArm64()
	linuxX64()
	mingwX64()
	wasmJs().browser()

	sourceSets {
		commonMain.dependencies {
			api(projects.math)
		}
	}
}
