@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinx.atomicfu)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.publish)
    dokka
    multiplatform
    powerAssert
}

dependencies {
    commonMainApi(libs.kotlinx.serialization)
    commonMainApi(libs.semver)
    commonMainApi(projects.utilIo)
}

kotlin {
    jvm()
    linuxArm64()
    linuxX64()
    mingwX64()
    wasmJs().browser()
}
