@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	multiplatform
}

dependencies {
	commonMainApi(projects.clientWgpu)
}

kotlin {
	jvm()
	linuxX64()
	mingwX64()
	wasmJs().browser()
}
