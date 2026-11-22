# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.6.0] - 2026-11-23

### CHANGED

- Upgraded Gradle to 9.9.2, Android Gradle Plugin to 9.5.2 and Kotlin to 2.4.21 (KSP 2.3.14)
- Switched Java toolchain to JDK 27 (bytecode target 26)
- Raised minSdk to 36 (Android 16), targetSdk to 37 and compileSdk to 37.2
- Upgraded dependencies: Compose BOM 2026.11.01, Hilt 2.60.1, AndroidX Lifecycle 2.11.1, Coroutines 1.11.0, Serialization 1.13.0, Dokka 2.3.0, MockK 1.14.12, JUnit 6.1.3
- Migrated to AGP built-in Kotlin support (removed `kotlin-android` plugin)
- Migrated detekt to 2.0.0-alpha.6 and regenerated `detekt.yml`
- Upgraded JaCoCo to 0.8.16 and updated coverage class directories
- Switched CI workflows to JDK 27

### FIXED

- Fixed compilation errors and detekt findings in instrumented tests

### REMOVED

- Deleted Qodana workflow and configuration

## [1.5.1] - 2025-11-18

### UPDATED

- Upgraded Gradle version

## [1.5.0] - 2025-11-01

### ADDED

- Created instrumented and unit tests

## [1.4.9] - 2025-10-22

### CHANGED

- Updated package's name

## [1.4.8] - 2025-10-22

### ADDED

- Created CI GitHub Actions for adding labels

## [1.4.7] - 2025-10-14

### CHANGED

- Change highlighting in pericope header

## [1.4.6] - 2025-10-13

### ADDED

- Added Codecov support

## [1.4.5] - 2025-10-13

### ADDED

- Qodama CI scripts

## [1.4.4] - 2025-10-13

### CHANGED

- Update GitHub Actions

## [1.4.3] - 2025-10-11

### FIXED

- Repair scripts and CodeQL

## [1.4.2] - 2025-10-11

### FIXED

- Set `android:allowBackup` to false

## [1.4.1] - 2025-10-11

### ADDED

- Created CHANGELOG.md
- Created CONTRIBUTING.md
- Created LICENSE

### CHANGED

- Updated README.md

## [1.4.0] - 2025-10-11

### ADDED

- Created *.md files
- Created CodeQL
- Created git hooks
- Created Dependabot

### CHANGED

- Updated Java folder to Kotlin
- Updated KSP version
- Updated GitHub Workflows scripts

### REMOVED

- Deleted `manes` plugin

## [1.3.3] - 2025-10-09

### Fixed

- Repair launcher image

## [1.3.2] - 2025-10-01

### Added

- Add pinch to zoom for pericope

### Changed

- Update gradle.properties

## [1.3.1] - 2025-08-09

### Changed

- Update dependencies
- Update V3 to V4 upload-artifacts in GHA

## [1.3.0] - 2025-05-22

### Added

- Add Hilt dependency
- Add GacApplication
- Add localized context
- Create SettingsRepository to inject in ViewModel
- Add CompositionLocalProvider for LocalContext
- Create AppLogger

### Changed

- Using Dimensions object for dimensions
- Add function extension isDarkTheme() for better support
- Updated dependencies
- Upgraded Gradle to 9.10
- Updated `ui.util` to `ui.helper`
- update README.md

### Fixed

- Pretending screen dimming
- Fixed some typos

### Removed

- Delete context dependency in viewModel
- Delete localViewModel
- Delete log entries in strings.xml
- Unused imports deleted

## [1.2.1] - 2025-05-15

### Fixed

- Change package names

## [1.2.0] - 2025-05-12

### Added

- Created Dokka support for project
- Created Detekt support for project

### Changed

- Refactoring package rk.gac to rk.powermilk.gac
- Upgrading Dokka to GDP v2
- Improving slider for ConfigSection
- Improving workflows

## [1.1.4] - 2025-05-12

### Added

- Created GHA workflows

### Changed

- Updated support from Android 9.0 (SDK 28)

## [1.1.3] - 2025-05-11

### Added

- Create tooltips for Settings
- Created mechanism to update config after change
- Created Draw pericope settings to config

### Fixed

- Repair onClose behaviour

### Changed

- Updated fallback strings

## [1.1.2] - 2025-05-07

### Added

- Created README.md
- Created R.string to enums
- Created string resources

### Changed

- Updated icon colors
- Updated CONDITIONAL bug
- Updated color scheme

## [1.1.1] - 2025-05-07

### Added

- Created first unit tests

## [1.1.0] - 2025-05-07

### Added

- Created first components
- Created Shuffle mechanics
- Created Screens for Pericope
- Created logo

## [1.0.1] - 2025-05-06

### Added

- Created config store
- Created SettingsScreen
- Created error display

## [1.0.0] - 2025-05-06

### Added

- initial commit
