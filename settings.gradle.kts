rootProject.name = "spock"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
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
	"toolkit",
	"util",
	"windowing"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
