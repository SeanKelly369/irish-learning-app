# Gaeilge

Gaeilge is a Kotlin Multiplatform foundation for learning Irish (Gaeilge), with shared Compose Multiplatform UI targeting Android and iOS.

## Prerequisites

- JDK 17 or newer (the project is configured to compile JVM sources for Java 17)
- Android Studio Ladybug or newer for Android development
- Xcode 16 or newer and CocoaPods/Swift tooling for iOS development
- A network connection on the first Gradle build so Gradle and Maven dependencies can be downloaded

The repository includes the Gradle wrapper. Generated build output is intentionally ignored by Git.

## Structure

```text
app/src/
  commonMain/  Shared Compose UI, domain models, repository, and strings
  commonTest/  Shared repository/domain tests
  androidMain/ Android activity and resources
  iosMain/     iOS Compose view-controller entry point
```

Packages are organized for feature growth: `app`, `core/designsystem`, `core/data`, `core/domain`, and `feature/home`. Future lesson and quiz screens belong in `feature/lessons` and `feature/quiz`.

## Run

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

Open the repository in Android Studio and run the `app` configuration on an Android emulator/device. For iOS, open the project in Xcode, configure the iOS application shell to call `MainViewController()`, and run an iOS simulator target. The shared Kotlin framework targets are already declared for Intel, Apple Silicon, and simulator builds.

## Next milestones

1. Add navigation and a real lessons list/detail flow.
2. Persist progress with a local data source and add dependency injection only when needed.
3. Add quiz exercises and Irish pronunciation/audio after the lesson model is stable.
4. Add localization resources for English and Gaeilge and accessibility coverage.

No backend, authentication, or audio dependencies are included in this foundation.
