@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	multiplatform
	dokka
	alias(libs.plugins.publish)
}

repositories.maven(url = "https://gitlab.com/api/v4/projects/25805863/packages/maven") { // TODO: remove
	name = "Wgpu4k"
}

kotlin {
	applyDefaultHierarchyTemplate()

	jvm().mainRun {
		mainClass = "quest.laxla.spock.MainKt"
	}

	linuxX64().binaries.executable {
		entryPoint = "quest.laxla.spock.mainBreaking"
	}

	wasmJs {
		moduleName = "spock"

		binaries.executable()
		browser {
			commonWebpackConfig {
				outputFileName = "spock.js"
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
			api(projects.util)
			api(libs.wgpu4k)
		}

		val glfwMain by creating {
			dependsOn(commonMain.get())
			dependencies {
				implementation(projects.glfw)
				implementation(libs.lwjgl.glfw)
			}
		}

		jvmMain {
			dependsOn(glfwMain)
			dependencies {
				implementation(libs.javaNativeAccess.platform)
				implementation(libs.wgpu4k.rococoa)
			}
		}

		nativeMain {
			dependsOn(glfwMain)
		}
	}
}
