rootProject.name = "spock"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
	"client",
	"client-wgpu",
	"client-wgpu-render-test",
	"core",
	"docs",
	"example",
	"glfw",
	"init",
	"lwjgl",
	"math",
	"math-base",
	"math-codegen",
	"util",
	"windowing"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
