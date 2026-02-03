@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.publish)
    dokka
    multiplatform
    powerAssert
}

dependencies {
    commonMainApi(libs.kotlinx.serialization)
    commonMainApi(libs.semver)
    commonMainApi(projects.util)
}

kotlin {
    jvm()

    linuxArm64().binaries {
        sharedLib()
        staticLib()
    }

    linuxX64().binaries {
        sharedLib()
        staticLib()
    }

    mingwX64().binaries {
        sharedLib()
        staticLib()
    }

    wasmJs {
        binaries.library()
        browser()
    }

    // TODO: KT-82284
    compilerOptions.allWarningsAsErrors = false

    sourceSets.all {
        languageSettings.enableLanguageFeature("ContextParameters")
    }
}
