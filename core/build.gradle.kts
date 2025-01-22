@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
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
