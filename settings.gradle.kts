rootProject.name = "spock"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
	"core",
	"docs",
	"example",
	"glfw",
	"lwjgl",
	"math",
	"math-base",
	"math-codegen",
	"toolkit",
	"util",
	"windowing"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
