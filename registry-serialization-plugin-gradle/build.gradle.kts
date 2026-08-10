import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import java.util.*

plugins {
	`java-gradle-plugin`
	jvm
}

dependencies {
	compileOnly(libs.kotlin.gradlePluginApi)
	implementation(projects.registrySerializationPluginCommon)
}

val String.artifact get() = "${rootProject.name}-$this"
val String.packg get() = "$group.${artifact.replace('-', '.')}"

gradlePlugin {
	plugins {
		create("spockRegistrySerializationPlugin") {
			id = projects.registrySerialization.name.packg
			implementationClass = "${name.packg}.SpockRegistrySerializationPlugin"
		}
	}
}

val file = layout.projectDirectory.file("src/main/generatedResources/metadata.properties")
val compilerPluginProject = projects.registrySerializationPluginKotlin


kotlin.sourceSets.main {
	resources.srcDirs(file.asFile.parentFile)
}

tasks {
	val generateResources = register("generateResources") {
		description = "Provide version data to gradle plugin"

		file.asFile.also { file ->
			outputs.files(file)
			file.ensureParentDirsCreated()

			Properties().also {
				it["version"] = version
				it["group"] = project.group
				it["artifact"] = compilerPluginProject.name.artifact
			}.store(file.outputStream(), null)
		}
	}

	processResources {
		dependsOn(generateResources)
	}
}

