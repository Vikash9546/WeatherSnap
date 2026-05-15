# WeatherSnap

WeatherSnap is a modern, production-ready Android application that allows users to search for live weather by city, view weather details, create weather reports with captured photos using CameraX, and save reports locally.

## Features
- **Live Weather Search:** Real-time geocoding and weather data using the Open-Meteo API.
- **Create Weather Report:** Attach a photo and custom notes to a snapshot of live weather data.
- **Custom Camera:** In-app CameraX integration with automatic JPEG compression.
- **Saved Reports:** View all locally saved reports in a responsive LazyColumn layout.
- **Robust Draft Recovery:** Lifecycle-safe draft restoration utilizing Room DB ensuring data survives configuration changes, process recreation, and app backgrounding.
- **Dark Theme:** Premium dark Material 3 design system with vibrant accents.

---

## Technical Stack & Requirements
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture Repository pattern
- **State Management:** ViewModel + StateFlow
- **Concurrency:** Kotlin Coroutines & Flow
- **Dependency Injection:** Dagger Hilt
- **Navigation:** Navigation Compose
- **Networking:** Retrofit + Gson + OkHttp (with logging interceptor)
- **Persistence:** Room Database
- **Camera integration:** CameraX
- **Image Loading:** Coil Compose
- **Minimum SDK:** 26
- **Target SDK:** 34
- **JDK Version:** Java 17

---

## Setup & Build Instructions

### Prerequisites
- **Android Studio Version:** Iguana | 2023.2.1 or newer (or Koala Feature Drop).
- **JDK Version:** Java 17 (Ensure your AS is configured to use JDK 17 for Gradle).

### Running the App
1. Clone or download the repository.
2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Set up an Android Emulator or connect a physical device (API 26+).
5. Build and run using the IDE run button, or via the command line:
   ```bash
   ./gradlew assembleDebug
   ```

---

## Architecture Explanation

The app strictly follows the **MVVM** pattern combined with the **Repository pattern**, structured into logical layers:
- **Domain:** Contains pure data models (`WeatherData`, `GeocodingResult`, etc.) and business logic representation.
- **Data:** Implements data sources. Uses Retrofit for remote API fetching (`GeocodingApiService`, `WeatherApiService`) and Room for local caching and persistence (`WeatherReportDao`, `ReportDraftDao`). Repositories are exposed here to abstract data origin from the ViewModels.
- **UI:** Exposes StateFlows from dedicated ViewModels for each screen. Composables observe these flows and update accordingly. No business logic is placed inside the Composables.
- **DI:** Hilt is used extensively to provide Singletons (Retrofit, Room) and inject repositories directly into ViewModels.

---

## Lifecycle Recovery Strategy

A robust draft recovery system handles scenario interruptions (rotation, backgrounding, process death):
- **Strategy:** I implemented a single-entity Room database table (`ReportDraftEntity`) for drafting.
- **Why Room over SavedStateHandle?** While `SavedStateHandle` handles process death, it can struggle with large payloads or file URIs when navigating away or if the app is fully terminated and cleared from memory over long periods. Storing the draft in Room guarantees the data is safely persisted. 
- **Implementation:** When `CreateReportScreen` is launched with a selected weather snapshot, `CreateReportViewModel` checks Room for an existing draft matching the city. If found, it automatically restores the notes, image path, and sizes. Any updates to notes or a newly captured image automatically overwrite the Room draft entity on a background thread.
- **Immutability:** The selected weather data snapshot is locked into memory/Draft DB. Even if the actual weather changes externally, the snapshot attached to the draft remains unchanged.
- **Completion:** Once the report is successfully finalized and saved to the main `weather_reports` table, the draft entity is cleared from Room.

---

## Image Compression Strategy

- **CameraX Capture:** The custom camera captures a high-resolution image directly to a temporary `File` in the app's cache directory.
- **Background Compression:** Once captured, `ImageCompressor.compress()` runs strictly on `Dispatchers.IO`. It decodes the captured file and recompresses it to an 80% quality JPEG format using `Bitmap.compress()`.
- **Sizing:** Both the original and compressed sizes are calculated and recorded. 
- **Temporary Storage:** The compressed file is stored in the app's internal `filesDir`, making it private to the app and keeping the gallery clean.

---

## Cleanup Strategy

- Temporary uncompressed images created by CameraX are immediately deleted after the compression process is complete.
- If the user captures multiple photos for the same report draft, the `CreateReportViewModel` safely deletes the previously compressed orphaned image before updating its state with the new one.
- Only the final, compressed image associated with a saved report remains in the app's `filesDir`.

---

## Open-Meteo APIs Used
- Geocoding API: `https://geocoding-api.open-meteo.com/v1/search`
- Weather Forecast API: `https://api.open-meteo.com/v1/forecast`

The app uses *only* Open-Meteo and relies on no mock data. Repeated suggestion queries are debounced and cached in-memory in the Repository layer to reduce network spam.
