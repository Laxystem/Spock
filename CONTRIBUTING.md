# Spock Contribution Guide

Contributions are welcome and accepted. Please create issues or pull requests!

> [!NOTE]
> This project is mainly developed [on Codeberg](https://codeberg.org/Laxystem/Spock);
> Please don't create pull requests on GitHub.
> I'll try to review them properly if you do, but it's a lot easier for me if everything is in one place.

## Kotlin Style Guide

### APIs

* APIs should be thread-safe and immutable.
* Expensive operations should be `suspend fun`s.
* To create a new API, *always* declare an interface or (for immutable data) a data class. If possible, declare it as a `fun interface` (see [Interfaces, Functional Interfaces, or Typealiases](#interfaces-functional-interfaces-or-typealiases) below).
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

#### Interfaces, Functional Interfaces, or Typealiases

* First, decide on your interface's contract.
* If you intend on declaring extensions on the interface, or having a complex contract, do not use a typealias.
* If you're unsure about a function's signature, mark it with `@ExperimentalSpockApi`.
* If you're sure about the current API, but not sure if you want to add more functions later, mark it with `@RequiresSubclassOptIn(ExperimentalSpockApi::class)`.

If you need to choose between having more than one function on your interface and `fun`ness, rhe non-functional interface is likely the better choice. 

If you intend on declaring extensions on the interface, do not use a typealias.

If an interface can be made functional, make it so.

Prefer a `@RequiresSubclassOptIn` annotation over `fun`ness.

See the [Kotlin documentation](https://kotlinlang.org/docs/fun-interfaces.html#functional-interfaces-vs-type-aliases) on the topic.

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

A link to the documentation link will be provided in the console,
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

