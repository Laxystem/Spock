@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	dokka
	multiplatform
	alias(libs.plugins.publish)
}

kotlin {
	jvm()

	linuxX64().binaries {
		staticLib()
		sharedLib()
	}

	mingwX64().binaries {
		staticLib()
		sharedLib()
	}

	sourceSets {
		commonMain.dependencies {
			api(projects.windowing)
		}

		jvmMain.dependencies {
			api(libs.lwjgl.glfw)
			implementation(libs.lwjgl)
			runtimeOnly(projects.lwjgl)
		}

		nativeMain.dependencies {
			implementation(libs.wgpu4k.glfw)
		}
	}
}
