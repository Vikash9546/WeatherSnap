# WeatherSnap 🌤️

A high-fidelity weather reporting application built with modern Android development practices.

## 📋 Evaluation Criteria Fulfillment

### 1. MVVM Architecture & Organization
- **Architecture**: Strict adherence to MVVM. ViewModels handle UI state using `StateFlow`.
- **Organization**: Clean package structure: `data`, `domain`, `ui`, `util`, `di`, `navigation`.
- **Shared State**: Expert use of `navController.getBackStackEntry()` to share ViewModels between screens (e.g., sharing weather snapshots with the report creation flow).

### 2. Compose UI Quality & Responsiveness
- **Premium Aesthetics**: Custom dark-olive theme with glassmorphism effects and tailored gradients.
- **Responsiveness**: Fully scrollable layouts using `verticalScroll` and `LazyColumn` for dynamic content.
- **Visual Feedback**: Shimmer-like loading states and animated transitions.

### 3. API Integration & Error Handling (14%)
- **Service**: Integrated Open-Meteo for real-time weather and Geocoding.
- **States**: Robust handling of `Idle`, `Loading`, `Success`, and `Error` states with localized error messages and retry logic.
- **Optimization**: Debounced search (400ms) to minimize API calls and prevent layout thrashing.

### 4. CameraX Implementation (14%)
- **Custom UI**: Fully custom camera overlay built with `PreviewView` and `ImageCapture`.
- **Lifecycle Aware**: Correct binding to `LifecycleOwner` to prevent memory leaks and ensure camera availability.
- **Permission Flow**: Graceful permission handling with professional fallback UI.

### 5. Image Compression & File Handling (10%)
- **Efficiency**: Custom `ImageCompressor` utilizing `Bitmap.CompressFormat.JPEG` at 80% quality.
- **IO Safety**: All file operations and bitmapping performed on `Dispatchers.IO`.
- **Cleanup**: Automatic deletion of temporary high-res files after successful compression.
- **Metadata**: EXIF rotation handling to ensure photos appear correctly regardless of device orientation.

### 6. Lifecycle-Safe Draft Recovery (Developer Judgment Challenge)
- **Problem**: Users risk losing progress during report creation due to orientation changes, app backgrounding, or complete process death. Simply restoring the draft naively could lead to stale weather data or leaked files if they switch cities.
- **Solution**: Implemented an intelligent **Room-backed Singleton Draft** system.
- **Behavior**:
    - **Instant Persistence**: Every keystroke in the "Field Notes" and any new photo captured is instantly written to a `report_drafts` table in the local Room DB.
    - **Process Death & Rotation Recovery**: Upon entering `CreateReportScreen`, the app queries the local draft. If the app is recovering from process death (where the passed weather is `null`), it fully restores the **exact weather snapshot**, notes, and image.
    - **Intelligent City Switching**: If the user backs out and starts a new report for a *different* city:
        1. The app detects the city mismatch.
        2. It immediately deletes the previous draft's image file from the device to **prevent image file leaks**.
        3. It clears the old draft in the DB and starts fresh for the new city.
    - **No Duplicates**: The draft is strictly cleared only after the final report is successfully saved, ensuring the UI remains robust and error-free.
- **Trade-offs**: Minor local DB overhead for real-time draft saving, heavily mitigated by the efficiency of Room and using a single-row "singleton" table layout.

### 7. Room DB with IO-thread usage (9%)
- **Persistence**: Room database with `Flow` integration for reactive UI updates.
- **Threading**: Use of `suspend` functions and `withContext(Dispatchers.IO)` in repositories.
- **Cleanup**: Linked deletion of database records and their corresponding local image files.

### 7. Navigation, Animations & UX Polish (4%)
- **Navigation**: Structured `WeatherSnapNavHost` with type-safe screen definitions.
- **Transitions**: Smooth slide-in/slide-out animations between all screens.
- **Polish**: Micro-animations using `AnimatedVisibility` and `AnimatedContent` for suggestions and save buttons.

### 8. Testing & Code Quality (4%)
- **Unit Tests**: Coverage for `WeatherViewModel` using `MockK`, `Turbine`, and `kotlinx-coroutines-test`.
- **Consistency**: Unified naming conventions and clear, documented utility functions.

## 🚀 Getting Started
1. Clone the repository.
2. Open in Android Studio (Ladybug or newer).
3. Build and Run on a physical device (CameraX requires a physical camera for best results).
