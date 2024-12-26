# Spock Game Engine

## Maven

All modules are available on maven central.
Snapshots are currently *not* available.

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

## Why Spock?
> [!CAUTION]
> Spock is in alpha.
> Backwards compatibility is not guaranteed, but I'm doing my best not to break it more than absolutely needed.

Spock is (or to be more exact, will be) a Kotlin/Multiplatform game engine.

The alternative, [KorGE](https://korge.org/), is in maintenance mode,
and has a knack out of creating its own libraries instead of using existing ones.

## Why Name it Spock?

Originally, this engine was supposed to use Vulkan,
but then I discovered [wgpu4k](https://GitHub.com/Wgpu4k/Wgpu4k) exists.
It'll likely be renamed to MoCKoGE, *The **Mo**dular **C**oncurrent **Ko**tlin **G**ame **E**ngine* at some point.
Previous (and admittedly, horribly designed) engines I've named the same are available
[here](https://github.com/Laxystem/MoCKoGE) and [here](https://github.com/Laxystem/MoCKoGE-Legacy).
This repository is intended to supersede both, though with more realistic goals.

## Why GitHub?

I'm a huge proponent of [Codeberg](https://codeberg.org).
However, GitHub still has infrastructure it does not yet support (better workflows, dependabot).
In the future, the [GitHub repository](https://github.com/Laxystem/Spock) will likely become a mirror
(though I'll still use it for dependabot and workflows), and issue management will move to Codeberg.

> [!NOTE]
> Codeberg does not currently support automatically fetching commits from GitHub.
> Therefore, I'll likely fetch merged PRs from GitHub and push them to codeberg manually.
