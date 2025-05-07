# 🎲 Gospel A Caso

**Gospel A Caso** is a minimalist Android app that helps you read the Gospel by selecting random Gospel pericopes (passages) — either on demand or triggered by device rotation. It supports multiple configuration modes and is inspired by spiritual reflection, randomness, and simplicity.

---

## ✨ Features

- 🔀 Randomly draw a pericope (passage) from the four Gospels.
- 🔧 Customize how many additional passages are shown before/after.
- ⚙️ Display mode: Light / Dark / Follow system.
- 📱 Draw mode: Button / Orientation change / Both.
- 💾 Configuration is saved between sessions.
- 📄 Clean material design using **Jetpack Compose**.
- 📚 Full Polish Gospel text (Biblia Jerozolimska).

---

## ⚙️ Configuration Options

- **Additional Pericopes**: None / Always / Only if selected text is short.
- **Threshold**: How many words is considered “short” (25–100).
- **Draw Mode**: Via button, by rotating the screen, or both.
- **Fallbacks**: Show next/previous if at Gospel's start or end.
- **Display Mode**: Light, Dark, or System default.

---

## 📁 Assets

- JSON data of the Gospel pericopes is stored in [`res/raw/pl_gospel.json`](./app/src/main/res/raw/pl_gospel.json).
- Application icon is a minimalist magenta die with a Bible, matching the app's aesthetic.

---

## 🚀 Getting Started

### Requirements

- Android Studio Hedgehog or later
- Android 12+ recommended (for Material You support)
- Kotlin 2.1+
- Gradle 8.9+

### Run the App

```bash
./gradlew installDebug
