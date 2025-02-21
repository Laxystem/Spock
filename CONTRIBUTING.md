# Spock Contribution Guide

Contributions are welcome and accepted. Please create issues or pull requests!

> [!NOTE]
> This project is developed [on Codeberg](https://codeberg.org/Laxystem/Spock);
> Due to [technical limitations](https://codeberg.org/forgejo/forgejo/issues/6829#issuecomment-2701381),
> pull requests aren't accepted from GitHub—any commit on the codeberg repository immeidately overrides any unmerged changes on GitHub.
> Migrating your repository to Codeberg [is easy](https://codeberg.org/repo/migrate?service_type=2&org=&mirror=), and you can create an account simply by logging in with your GitHub one.

## Kotlin Style Guide

*Don't worry too much about this part, tweaking an API to match the style guide is a relatively easy task.*

### APIs

* APIs should be thread-safe and immutable by default.
* Expensive operations should be `suspend fun`s.
* To create a new API, *always* declare an interface or (for immutable data) a data class. If possible, declare it as a `fun interface` (see [Interfaces, Functional Interfaces, or Typealiases](#interfaces-functional-interfaces-or-typealiases) below).
    * *Never* expose non-`annotation`, non-`data` classes as public API, especially `abstract` ones.
    * Take care when exposing `data` classes as public API: if you intend on giving them more properties, annotate them with `@ExperimentalSpockApi`, and the constructor with `@JvmOverloads`; The automatically-generated `copy` function will break when adding more properties.
    * Prefer composition (e.g. [interface delegation](https://kotlinlang.org/docs/delegation.html)) over inheritance.
* Implementations should be instantiated via extension functions (e.g. `ByteArray.appender()`) or functions named the same or as if they were a subtype of as the interface (e.g. `ByteAppender()`, `NonThrowingByteAppender()`).
* If an implementation accepts a lambda as a parameter (e.g. `EverlastingMutex(producer: (Descriptor) -> Product)`), it should be implemented, with the lambda marked `crossinline`. Otherwise, declare a private class (usually called `FooImpl`).
    * Mark the implementation class (or if inline, the function) with the `@author` KDoc tag.
* Always expose everything necessary for users to reimplement a built-in API. If some API is lower-level and should not be used except for this specific purpsoe, mark it with `@RawSpockApi`.
* If an API allows easily breaking the contract of another, mark it with `@DelicateSpockApi`.
* If you're unsure about an API, mark it with `@ExperimentalSpockApi`.
* Document your APIs, and always use the `@since` KDoc tag.
   * When removing the `@ExprimentalSpockApi` annotation, don't change the `@since` tag.
   * When breaking ABI *or* API compatibility, reset the `@since` tag.
* Always use `@Throws` when applicable, including the `@throws` KDoc tag.
* Prefer descriptive type parameter names; for example, `Key` and `Value` are better than `K` and `V`.
    * Prefer an existing type variable convention  (`K` and `V` for maps) over creating your own.
* Except for `: Any`, prefer using `where` for type variables.
* For type variables, prefer using `T & Any` over `T : Any`, unless you use a type that explicitly bounds `T` to extend `Any` (not be nullable).

#### Interfaces, Functional Interfaces, or Typealiases

* First, decide on your interface's contract.
* If you intend on declaring extensions on the interface, or having a complex contract, do not use a typealias.
* If you're unsure about a member function's signature, mark it with `@ExperimentalSpockApi`.
* If you're sure about the current API, but not sure if you want to add more functions later, mark it with `@RequiresSubclassOptIn(ExperimentalSpockApi::class)`.
* Do not insist on only having one function.
* Do not insist on using an `operator fun invoke`.
* Do not insist on using a function in favor or a property.
* If the interface can be made functional, make it so.
* See the [Kotlin documentation](https://kotlinlang.org/docs/fun-interfaces.html#functional-interfaces-vs-type-aliases) on the topic.

### Implementation

* Prefer nullability over `::prop.isInitialized`.
* Prefer `Flag` over `lateinit var: Unit`.

## Useful Commands

> [!TIP]
> All commands are also available as IntelliJ run configurations in the [`.run`](.run) directory.

### Run All Tests

```bash
./gradlew allTests --quiet --stacktrace
```

### Generate Documentation

```bash
./gradlew :docs:dokkaGenerate --stacktrace
```

> [!IMPORTANT]
> Do not commit the [`docs/history`](docs/history) directory yourself;
> It'll be generated automatically.

A link to the rendered documentation will be provided in the console,
in the form of `localhost:PORT/spock/docs/current/index.html`,
usually [on port 63342](http://localhost:63342/spock/docs/current/index.html).

### Run JVM Example

```bash
./gradlew :example:jvmRun --quiet --stacktrace
```

### Run Native Example

> [!NOTE]
> Replace `Platform` below with your operating system; for example, `LinuxX64`.

#### Debug

```bash
./gradlew :example:runDebugExecutablePlatform --quiet --stacktrace
```

#### Release

```bash
./gradlew :example:runReleaseExecutablePlatform --quiet --stacktrace
```

### WasmJS

#### Development

```bash
./gradlew :example:wasmJsBrowserDevelopmentRun --quiet --stacktrace
```

#### Production

```bash
./gradlew :example:wasmJsBrowserDevelopmentRun --quiet --stacktrace
```

### Dump ABI

```bash
./gradlew apiDump --quiet --stacktrace
```

### Generate Test Coverage Report

```bash
./gradlew koverHtmlReport --quiet --stacktrace
```
