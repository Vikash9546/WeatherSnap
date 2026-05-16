# WeatherSnap 🌤️📸

A production-grade Android weather reporting app built with Kotlin, Jetpack Compose, Hilt, Room, CameraX, and Retrofit.

---

## 🛠️ Tech Stack

| Category | Technology |
|:---|:---|
| Language | Kotlin 2.0 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt 2.52 |
| Database | Room 2.6 |
| Networking | Retrofit 2.11 + OkHttp 4.12 |
| Camera | CameraX 1.3 |
| Image Loading | Coil 2.7 |
| Testing | MockK, Turbine, Coroutines Test |

---

## 🚀 Setup & Run

### Prerequisites
- Android Studio **Ladybug** (2024.2+) or newer
- JDK 17+
- Physical Android device (CameraX requires a real camera)

### Steps

```bash
# 1. Clone
git clone https://github.com/Vikash9546/WeatherSnap.git
cd WeatherSnap

# 2. Build
./gradlew assembleDebug

# 3. Install on connected device
./gradlew installDebug

# 4. Run unit tests
./gradlew testDebugUnitTest
```

Or simply open the project in Android Studio and press **Run ▶️**.

> **Note**: No API key needed — WeatherSnap uses the free [Open-Meteo API](https://open-meteo.com/).

---

## 🏗️ Architecture

Clean Architecture with MVVM — three layers, unidirectional data flow:

```
UI (Compose Screens + ViewModels)
  → Domain (Use Cases + Models)
    → Data (Repositories + Room + Retrofit)
```

**Package structure:**
```
com.weathersnap.app/
├── data/          # Room DB, Retrofit APIs, Repositories, Mappers
├── domain/        # Use Cases, Domain Models
├── ui/            # Screens (weather, create_report, camera, reports), Theme
├── navigation/    # NavHost with animated transitions
├── di/            # Hilt modules (Database, Network)
└── util/          # ImageCompressor, WeatherCodeUtil
```

---

## 📱 Features

- **Weather Search** — Debounced city search with Open-Meteo geocoding + weather API
- **Create Report** — Capture photo, compress, annotate with notes, save locally
- **Custom Camera** — CameraX with EXIF rotation handling and JPEG compression (80% quality)
- **Saved Reports** — Browse and delete reports with linked image cleanup
- **Draft Recovery** — Room-backed singleton draft survives rotation and process death

---

## 🛡️ Developer Judgment: Lifecycle-Safe Draft Recovery

**Problem**: User creates a report (selects weather → captures photo → enters notes) then rotates or backgrounds the app. Progress must survive without duplicates.

**Solution**: Room-backed singleton draft (`id = 1`, `OnConflictStrategy.REPLACE`).

| Scenario | Behavior |
|:---|:---|
| Rotation | ViewModel survives; Room draft is safety net |
| Process death | Draft restored from Room with **exact weather snapshot** |
| City switch | Old draft image deleted, draft cleared, fresh start |
| Save | Draft cleared after successful insert to reports table |
| Discard | `discardDraft()` deletes temp image + clears Room |

**Key guarantees:**
- Weather snapshot is persisted in the draft (including `weatherCode`) — never re-fetched
- Singleton draft = no duplicates possible
- Temp images cleaned at 4 points: retake, city switch, discard, report deletion

**Trade-offs:** Minor Room write per keystroke (~0.1ms for single-row), explicit cleanup instead of GC sweep.

**Files:** `CreateReportViewModel.kt`, `ReportDraftEntity.kt`, `ReportDraftDao.kt`, `ReportDraftRepository.kt`, `ImageCompressor.kt`

---

## 📄 License

For educational and evaluation purposes.
