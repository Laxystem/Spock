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

	linuxArm64().binaries {
		sharedLib()
		staticLib()
	}

	linuxX64().binaries {
		sharedLib()
		staticLib()
	}

	mingwX64().binaries {
		sharedLib()
		staticLib()
	}

	wasmJs {
		browser()
		binaries.library()
	}
}
