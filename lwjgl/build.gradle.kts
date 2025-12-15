plugins {
	jvm
	alias(libs.plugins.publish)
}

dependencies {
	for (classifier in setOf(
		"natives-freebsd",
		"natives-linux",
		"natives-linux-arm64",
		"natives-linux-riscv64",
		"natives-macos-arm64",
		"natives-windows"
	)) {
		api(variantOf(libs.lwjgl) {
			classifier(classifier)
		})
		api(variantOf(libs.lwjgl.glfw) {
			classifier(classifier)
		})
	}
}
