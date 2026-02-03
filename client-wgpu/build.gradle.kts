@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

kotlin {
	applyDefaultHierarchyTemplate {
		common {
			group("glfw") {
				withJvm()
				group("native") {
					withNative()
				}
			}
		}
	}

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

	sourceSets {
		commonMain.dependencies {
			api(libs.wgpu4k)
			api(libs.wgpu4k.ktypes.descriptors)
			api(projects.client)
			api(projects.windowing)
		}

		named("glfwMain").dependencies {
			implementation(projects.glfw)
		}

		jvmMain.dependencies {
			implementation(libs.javaNativeAccess.platform)
			implementation(libs.wgpu4k.rococoa)
		}
	}
}
