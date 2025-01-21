@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	multiplatform
	dokka
	alias(libs.plugins.publish)
}

dependencies {
	commonMainApi(projects.math)
	commonMainApi(projects.util)
}

kotlin {
	applyDefaultHierarchyTemplate()

	jvm()
	linuxX64()

	wasmJs {
		moduleName = "spock"

		binaries.library()
		browser()
	}
}
