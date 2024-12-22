import org.gradle.internal.jvm.inspection.JvmVendor

plugins {
	kotlin("jvm")
}

val jvm: String by properties

kotlin {
	explicitApi()
	jvmToolchain(jvm.toInt())
}

