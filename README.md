# Print

A Kotlin Multiplatform logging library with support for terminal, file, network, SigNoz, and Firebase output - plus global crash handling across coroutines, threads, native signals, and browser exceptions.

---

## Platform Support

| Platform | Console | File | Network | SigNoz | Firebase | Crash Handling |
|---|---|---|---|---|---|---|
| Android | + | + | + | + | + | + |
| iOS | + | + | + | + | + | + |
| Desktop JVM | + | + | + | + | + | + |
| Desktop Native (MinGW / Linux / POSIX) | + | + | + | + | + | + |
| Browser (JS) | + | + (IndexedDB) | + | + | + | + |

---

## Installation

Add the dependency to your shared module:

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.cheerwizard:print:1.0.1")
}
```

---

## Setup

Call `Print.install()` once at the entry point of your app. All code inside the trailing lambda is guarded by Print's crash handlers — any uncaught exception will be caught and reported through your configured loggers.

```kotlin
Print.install(
    logLevel = LogLevel.WARNING,                             // optional, default is VERBOSE
    crashReportFilepath = "logs/crash-report-2026-01-10.log", // optional, default is logs/crash-report-{currentDate}.log
    loggers = setOf(
        ConsoleLogger(),
        FileLogger(filepath),
        SignozLogger(host),
        FirebaseLogger(clientId, secret, measurementId),
        CustomLogger()           // implement Logger or NetworkLogger
    )
) {
    // your app entry point code here
    // crashes in this block are caught and reported automatically
}
```

The `context` parameter is required — it gives Print access to platform-specific functions such as file I/O paths, native signal registration, and platform metadata attached to log events.

---

## Log Levels

```kotlin
enum class LogLevel {
    NONE,       // disables all logging
    VERBOSE,    // everything (default)
    INFO,
    DEBUG,
    WARNING,
    ERROR,
    FATAL
}
```

The `logLevel` set at install time acts as a global minimum — loggers will not receive events below it.

---

## Logging

```kotlin
Print.v("MyTag", "Verbose message")
Print.i("MyTag", "Info message")
Print.d("MyTag", "Debug message")
Print.w("MyTag", "Warning message")
Print.e("MyTag", "Error message")
Print.e("MyTag", Throwable())
Print.f("MyTag", "Fatal message")
Print.f("MyTag", Throwable())
```

---

## Loggers

### ConsoleLogger

Logs to the platform's standard output with log level labels.

```kotlin
ConsoleLogger()
```

### FileLogger

Persists logs to a local file in append mode. Survives app restarts and is the target for crash reports.

On the browser platform, logs are stored in `window.indexedDB` when available.

```kotlin
FileLogger(filepath = "/data/logs/app.log")
```

### SignozLogger

Sends structured logs to a [SigNoz](https://signoz.io) endpoint. Events are forwarded as structured JSON with level, tag, timestamp, and platform metadata.

```kotlin
SignozLogger(host = "https://ingest.signoz.io")
```

### FirebaseLogger

Sends logs and events to Firebase Analytics / Crashlytics.

```kotlin
FirebaseLogger(
    clientId = "your-client-id",
    secret = "your-secret",
    measurementId = "G-XXXXXXXXXX"
)
```

### CustomLogger

Implement the `Logger` interface for simple loggers, or `NetworkLogger` for loggers that send data over the network:

```kotlin
class MyLogger : Logger {
    override fun log(level: LogLevel, tag: String, message: String) {
        // your implementation
    }
}

class MyNetworkLogger : NetworkLogger {
    override suspend fun send(level: LogLevel, tag: String, message: String) {
        // your implementation
    }
}

Print.install(
    // ...
    loggers = setOf(MyLogger(), MyNetworkLogger())
)
```

---

## Crash Handling

Crash handling is enabled automatically for all code inside the `Print.install {}` lambda. No separate setup is needed.

| Crash Type | Platforms |
|---|---|
| Coroutine exceptions (`CoroutineExceptionHandler`) | All |
| Thread uncaught exceptions (`Thread.setDefaultUncaughtExceptionHandler`) | JVM, Android |
| Native signal crashes (`SIGSEGV`, `SIGABRT`, `SIGFPE`, `SIGILL`, `SIGBUS`) | Android, Desktop JVM, Desktop Native |
| Browser uncaught exceptions (`window.onerror`, `unhandledrejection`) | Browser |
| iOS uncaught exceptions (`NSException`) | iOS |

Native crash reports are written synchronously using async-signal-safe I/O, so the report is preserved even when the process is about to terminate.

---

## License

[MIT](./LICENSE)
