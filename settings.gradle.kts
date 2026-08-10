rootProject.name = "spock"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")

include(
	"client",
	"client-wgpu",
	"client-wgpu-render-test",
	"client-wgpu-renderer",
	"core",
	"docs",
	"ecs",
	"example",
	"glfw",
	"init",
	"lwjgl",
	"math",
	"math-base",
	"math-codegen",
	"registry",
	"registry-serialization",
	"registry-serialization-plugin-common",
	"registry-serialization-plugin-kotlin",
	"registry-serialization-plugin-gradle",
	"util",
	"util-io",
	"windowing"
)

@Suppress("UnstableApiUsage")
dependencyResolutionManagement.repositories.mavenCentral()
