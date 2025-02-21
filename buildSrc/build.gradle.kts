plugins {
	`kotlin-dsl`
}

fun PluginDependency.asLibrary(): Any = "$pluginId:$pluginId.gradle.plugin:$version"
fun Provider<PluginDependency>.asLibrary(): Provider<Any> = map { it.asLibrary() }

dependencies {
	implementation(libs.plugins.dokka.asLibrary())
	implementation(libs.plugins.kotlin.jvm.asLibrary())
	implementation(libs.plugins.kotlin.multiplatform.asLibrary())
	implementation(libs.plugins.kotlinx.binaryCompatibilityValidator.asLibrary())
	implementation(libs.plugins.kotlinx.kover.asLibrary())
	implementation(libs.plugins.kotlinx.powerAssert.asLibrary())
}
