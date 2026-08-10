import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
	jvm
}

dependencies {
	implementation(projects.registrySerializationPluginCommon)
	compileOnly(libs.kotlin.compiler.embeddable)
}

kotlin {
	explicitApi = ExplicitApiMode.Disabled

	compilerOptions.optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
