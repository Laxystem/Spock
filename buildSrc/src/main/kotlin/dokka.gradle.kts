plugins {
	id("org.jetbrains.dokka")
}

dokka {
	dokkaSourceSets.configureEach {
		sourceLink {
			remoteUrl("https://github.com/Laxystem/spock/tree/v$version")
			localDirectory = rootDir
			remoteLineSuffix = "#L"
		}
	}
}
