plugins {
	jvm
}

dependencies {
	implementation(libs.kotlinPoet)
	implementation(libs.kotlinPoet.kotlinSymbolProcessing)
	implementation(libs.kotlinSymbolProcessing)
	implementation(projects.mathBase)
}
