plugins {
	id("org.jetbrains.dokka")
}

dokka {
	dokkaSourceSets.configureEach {
		sourceLink {
			remoteUrl("https://github.com/Laxystem/spock/tree/master")
			localDirectory = rootDir
			remoteLineSuffix = "#L"
		}
	}
}
