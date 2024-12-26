plugins {
	jvm
	alias(libs.plugins.publish)
}

dependencies {
	for (classifier in setOf(
		"natives-freebsd",
		"natives-linux",
		"natives-linux-arm32",
		"natives-linux-arm64",
		"natives-linux-ppc64le",
		"natives-linux-riscv64",
		"natives-macos",
		"natives-macos-arm64",
		"natives-windows",
		"natives-windows-arm64",
		"natives-windows-x86"
	)) {
		api(variantOf(libs.lwjgl) {
			classifier(classifier)
		})
		api(variantOf(libs.lwjgl.glfw) {
			classifier(classifier)
		})
	}
}
