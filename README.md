# Spock Game Engine

> [!CAUTION]
> Spock is in alpha.
> Backwards compatibility is intended but not guaranteed.

## Maven

All modules are available on maven central.

<!-- TODO: support snapshot repo, separate release.yml to pages and publish jobs and move maven central secrets to another env -->

```kotlin
// build.gradle.kts

repositories.mavenCentral()

val spock: String by properties

dependencies {
	// core
	implementation("quest.laxla:spock:$spock")

	// any other module, e.g. util
	implementation("quest.laxla:spock-util:$spock")
}
```
