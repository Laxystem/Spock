@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	multiplatform
	dokka
	alias(libs.plugins.publish)
}

dependencies {
	commonMainApi(projects.util)
}

kotlin {
	applyDefaultHierarchyTemplate()

	jvm()
	linuxX64()

	wasmJs {
		moduleName = "spock"

		binaries.library()
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
}
