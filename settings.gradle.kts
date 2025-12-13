rootProject.name = "spock"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
	"client",
	"client-toolkit",
	"core",
	"docs",
	"example",
	"glfw",
	"init",
	"lwjgl",
	"math",
	"math-base",
	"math-codegen",
	"render-test",
	"util",
	"windowing"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
