@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	multiplatform
}

repositories.maven(url = "https://gitlab.com/api/v4/projects/25805863/packages/maven") { // TODO: remove
	name = "Wgpu4k"
}

dependencies {
	commonMainApi(projects.toolkit)

	// TODO: remove below dependencies, KT-74152
	commonMainApi(projects.core)
	commonMainApi(projects.util)
	commonMainApi(libs.wgpu4k)
}

kotlin {
	linuxX64().binaries.executable {
		entryPoint = "quest.laxla.spock.example.main"
	}
	jvm().mainRun {
		mainClass = "quest.laxla.spock.example.MainKt"
	}
	wasmJs {
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
}
