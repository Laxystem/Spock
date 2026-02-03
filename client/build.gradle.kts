@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

dependencies {
	commonMainApi(libs.wgpu4k.ktypes)
	commonMainApi(projects.math)
	commonMainApi(projects.util)
}

kotlin {
	jvm()

	linuxX64().binaries {
		sharedLib()
		staticLib()
	}

	mingwX64().binaries {
		sharedLib()
		staticLib()
	}

	wasmJs {
		binaries.library()
		browser()
	}
}
