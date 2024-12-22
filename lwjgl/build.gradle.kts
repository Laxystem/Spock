plugins {
	jvm
}

repositories.mavenCentral()

val classifier = "natives-linux"

dependencies {
	api(variantOf(libs.lwjgl) {
		classifier(classifier)
	})
	api(variantOf(libs.lwjgl.glfw) {
		classifier(classifier)
	})
}
