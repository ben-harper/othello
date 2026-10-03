# 🎮 Othello (Reversi) for Android

> 🤖 **Note**: This application was 100% designed, planned, coded, and deployed using **AI in Google Antigravity**!

A single-player Othello (Reversi) game built for Android using **Kotlin** and **Jetpack Compose**. The player controls Black (moving first) against a computer opponent using a positional weight strategy.

---

## ✨ Features

- **Custom App Icon**: Elegant wood-framed icon with glossy 3D black and white discs.
- **Jetpack Compose UI**: Clean, declarative UI with custom Canvas rendering.
- **Classic Board Styling**: Wood frame border with green felt grid and star markers.
- **3D Disc Graphics & Animations**: Dynamic radial lighting on discs, place animations, and capture flip transitions.
- **Medium-Difficulty AI**: Evaluates moves using positional square weights (prioritizing corners and edges, avoiding danger squares) and mobility heuristics.
- **Score Bar & Turn Tracker**: Live disc count, turn status, and an indicator when the AI is calculating its move.
- **Score History & Statistics**: Automatically records win/loss/draw records, win rate percentage, highest disc score, and average score per game with persistence across sessions.
- **Settings Screen**:
  - Toggle **Show Valid Moves** on or off at any time.
  - View overall score statistics and career record.
  - Option to reset score history.
- **Move Undo**: One-tap undo that rolls back both the AI response and player move.
- **Game Over Dialog**: End-game banner announcing the winner/draw with final score tallies and a "Play Again" button.

---

## 🛠️ Requirements & Tech Stack

- **Language**: Kotlin 2.3+
- **UI Toolkit**: Jetpack Compose (Material3)
- **Architecture**: MVVM with AndroidViewModel & SharedPreferences persistence
- **Build System**: Gradle 9.1 with Android Gradle Plugin 9.0+
- **JDK**: Java 17 (Eclipse Temurin)
- **Min SDK**: API 24 (Android 7.0+)
- **Target SDK**: API 36

---

## 🚀 Building & Running

### 1. Build the APK via Gradle

Open a terminal in the root directory:

```bash
# Windows
.\gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

The output APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Testing & Deployment

### Option A: Direct Download via GitHub Releases

You can download the ready-to-install `.apk` directly to your phone from the [Releases page](https://github.com/ben-harper/othello/releases).

---

### Option B: Test on Desktop Android Emulator

1. Launch your virtual device (e.g. `medium_phone`):
   ```bash
   # Windows helper script
   .\start_emulator.bat

   # Or via SDK emulator tool directly:
   emulator -avd medium_phone
   ```
2. Once the emulator window is open and booted, install and launch the app:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   adb shell monkey -p com.example.othello -c android.intent.category.LAUNCHER 1
   ```

---

### Option C: Deploy to a Physical Android Phone (USB)

1. **Enable Developer Options**:
   - Go to **Settings > About Phone**.
   - Tap **Build Number** 7 times until you see *"You are now a developer!"*.
2. **Enable USB Debugging**:
   - Go to **Settings > System > Developer Options**.
   - Toggle **USB Debugging** on.
3. **Connect Your Phone via USB**:
   - Connect the device to your computer.
   - Accept the *"Allow USB debugging?"* prompt on your phone screen.
4. **Install & Run**:
   ```bash
   adb devices
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   adb shell monkey -p com.example.othello -c android.intent.category.LAUNCHER 1
   ```
