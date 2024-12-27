@file:OptIn(ExperimentalKotlinGradlePluginApi::class, ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
}

repositories.maven(url = "https://gitlab.com/api/v4/projects/25805863/packages/maven") { // TODO: remove
	name = "Wgpu4k"
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
		browser {
			commonWebpackConfig {
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static = (static ?: mutableListOf()).apply {
						// Serve sources to debug inside browser
						add(project.rootDir.path)
						add(project.projectDir.path)
					}
				}
			}
		}
	}

	sourceSets {
		commonMain.dependencies {
			api(libs.wgpu4k)
			api(projects.core)

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
