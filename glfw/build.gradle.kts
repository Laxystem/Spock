@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	multiplatform
	dokka
}

repositories.mavenCentral()

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
	
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
