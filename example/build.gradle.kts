@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	multiplatform
}

dependencies {
	commonMainApi(projects.clientToolkit)
	commonMainImplementation(projects.init)
}

kotlin {
	jvm().mainRun {
		mainClass = "quest.laxla.spock.example.MainKt"
	}

	linuxX64().binaries.executable {
		entryPoint = "quest.laxla.spock.example.main"
	}

	mingwX64().binaries.executable {
		entryPoint = "quest.laxla.spock.example.main"
	}

	wasmJs {
		binaries.executable()
		browser {
			commonWebpackConfig {
				outputFileName = "spock.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
					static(project.rootDir.path)
					static(project.projectDir.path)
				}
			}
		}
	}
}
