@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	alias(libs.plugins.publish)
	dokka
	multiplatform
	powerAssert
}

kotlin {
	jvm()
	linuxArm64()
	linuxX64()
	mingwX64()
	wasmJs().browser {
		// TODO: fix
		/*testTask {
			useKarma().useChromiumHeadless()
		}
		 */
	}

	sourceSets {
		commonMain.dependencies {
			api(libs.kotlinx.io)
			api(projects.util)
		}

		commonTest.dependencies {
			implementation(kotlin("test"))
			implementation(libs.kotlinx.coroutines.test)
		}
	}
}
