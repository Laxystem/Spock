# Spock Engine

> [!NOTE]
> Spock is [developed on Codeberg](https://codeberg.org/Laxystem/Spock), and [mirrored on GitHub](https://GitHub.com/Laxystem/Spock).

> [!CAUTION]
> Spock is in alpha.
> Backwards compatibility is intended but not guaranteed.

## Maven

All modules are available on maven central.

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
