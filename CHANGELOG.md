# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.4.5] - 2025-10-11

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

- Refactoring package rk.gac to pl.rk.gac
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
