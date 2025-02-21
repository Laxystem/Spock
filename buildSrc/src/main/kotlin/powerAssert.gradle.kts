@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
	id("org.jetbrains.kotlinx.kover")
	kotlin("plugin.power-assert")
}

powerAssert {
	includedSourceSets.addAll(project
		.the<SourceSetContainer>()
		.asSequence()
		.filterIsInstance<KotlinSourceSet>()
		.map { it.name }
		.filter { it.contains("test", ignoreCase = true) }
		.toList()
	)
	functions.add("kotlin.test.assertTrue")
}
