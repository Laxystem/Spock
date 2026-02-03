@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	multiplatform
	dokka
}

dependencies {
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
