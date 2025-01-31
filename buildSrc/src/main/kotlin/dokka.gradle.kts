plugins {
	id("org.jetbrains.dokka")
}

dokka {
	dokkaSourceSets.configureEach {
		sourceLink {
			remoteUrl("https://codeberg.org/Laxystem/Spock/src/tag/v$version")
			localDirectory = rootDir
			remoteLineSuffix = "#L"
		}
	}
}
