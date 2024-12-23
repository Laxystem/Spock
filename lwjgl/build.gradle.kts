plugins {
	jvm
	alias(libs.plugins.publish)
}

val classifier = "natives-linux" // TODO: put all classifiers here

dependencies {
	api(variantOf(libs.lwjgl) {
		classifier(classifier)
	})
	api(variantOf(libs.lwjgl.glfw) {
		classifier(classifier)
	})
}
