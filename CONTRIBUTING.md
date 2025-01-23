# Spock Contribution Guide

> [!NOTE]
> This contribution guide is still work-in-progress.

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
> Do not commit the [`docs/history`](docs/history) yourself;
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

