plugins {
	dokka
}

dependencies {
	dokka(projects.core)
	dokka(projects.glfw)
	dokka(projects.util)
	
	dokkaHtmlPlugin(libs.dokka.versioning)
}

val docsDir = layout.projectDirectory.dir("history").apply { asFile.mkdirs() }

dokka {
	moduleName = rootProject.name
	
	dokkaPublications.html {
		includes.from(rootProject.file("README.md"))
	}

	pluginsConfiguration.versioning {
		version = project.version.toString()
		olderVersionsDir = docsDir
	}
}

tasks.dokkaGeneratePublicationHtml {
	val targetDir = layout.projectDirectory.dir("current")
	outputDirectory = targetDir

	doLast {
		val versionDir = docsDir.asFile.resolve(version.toString())
		targetDir.asFile.copyRecursively(versionDir, overwrite = true)
		versionDir.resolve("older").deleteRecursively()
	}
}
