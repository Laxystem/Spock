@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

repositories.mavenCentral()

kotlin {
	explicitApi()

	jvm()
	linuxX64()
	
	compilerOptions {
		freeCompilerArgs.add("-Xexpect-actual-classes")
	}

	sourceSets {
		commonMain.dependencies {
			api(projects.util)
		}

		jvmMain.dependencies {
			implementation(libs.lwjgl)
			implementation(libs.lwjgl.glfw)
			runtimeOnly(projects.lwjgl)
		}
		
		nativeMain.dependencies {
			implementation(libs.wgpu4k.glfw)
		}
	}
}
