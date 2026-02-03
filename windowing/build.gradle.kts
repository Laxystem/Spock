@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
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
		binaries.library()
		browser()
	}

	sourceSets {
		commonMain.dependencies {
			api(projects.math)
		}
	}
}
