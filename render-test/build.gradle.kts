@file:OptIn(ExperimentalWasmDsl::class, ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	multiplatform
}

dependencies {
	commonMainApi(projects.clientWgpu)
	commonMainImplementation(projects.init)
}

kotlin {
	jvm().mainRun {
		mainClass = "quest.laxla.spock.renderTest.MainKt"
	}

	linuxX64().binaries.executable {
		entryPoint = "quest.laxla.spock.renderTest.main"
	}

	mingwX64().binaries.executable {
		entryPoint = "quest.laxla.spock.renderTest.main"
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
