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
	linuxX64()

	wasmJs {
		moduleName = "spock"

		binaries.library()
		browser()
	}

	sourceSets {
		commonMain.dependencies {
			api(libs.wgpu4k)
			api(projects.core)
			api(projects.windowing)

			// TODO: remove below dependencies, KT-74152
			api(projects.util)
		}

		named("glfwMain").dependencies {
			implementation(libs.lwjgl.glfw)
			implementation(projects.glfw)
		}

		jvmMain.dependencies {
			implementation(libs.javaNativeAccess.platform)
			implementation(libs.wgpu4k.rococoa)
		}
	}
}
