# WeatherSnap 🌤️📸

A production-grade Android weather reporting application built with **Kotlin**, **Jetpack Compose**, and modern Android architecture. WeatherSnap allows users to search for real-time weather data, capture geo-tagged weather photos using a custom CameraX implementation, and persist annotated reports locally with Room.

---

## 📱 Screens

| Weather Home | Create Report | Custom Camera | Saved Reports |
|:---:|:---:|:---:|:---:|
| Search & view weather | Annotate with photo & notes | Custom CameraX overlay | Browse & manage reports |

---

## 🏗️ Architecture

WeatherSnap follows **Clean Architecture** with strict **MVVM** separation across three layers:

```
┌──────────────────────────────────────────────────────┐
│                      UI Layer                        │
│  ┌────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  Screens   │  │  ViewModels  │  │   UiStates   │ │
│  │ (Compose)  │──│  (StateFlow) │──│   (Sealed)   │ │
│  └────────────┘  └──────────────┘  └──────────────┘ │
├──────────────────────────────────────────────────────┤
│                    Domain Layer                      │
│  ┌────────────────────┐  ┌─────────────────────────┐ │
│  │     Use Cases      │  │     Domain Models       │ │
│  │ SearchCityUseCase  │  │  WeatherData            │ │
│  │ GetWeatherUseCase  │  │  GeocodingResult        │ │
│  │ SaveReportUseCase  │  │  CameraResult           │ │
│  │ DeleteReportUseCase│  │                         │ │
│  │ GetAllReportsUseCase│ │                         │ │
│  └────────────────────┘  └─────────────────────────┘ │
├──────────────────────────────────────────────────────┤
│                     Data Layer                       │
│  ┌────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Remote   │  │ Repositories │  │    Local     │ │
│  │ Retrofit   │──│ Single Source│──│ Room DB      │ │
│  │ API Calls  │  │  of Truth   │  │ DAOs         │ │
│  └────────────┘  └──────────────┘  └──────────────┘ │
└──────────────────────────────────────────────────────┘
```

### Package Structure

```
com.weathersnap.app/
├── data/
│   ├── local/              # Room DB, DAOs, Entities
│   ├── remote/             # Retrofit API services, DTOs
│   ├── mapper/             # DTO ↔ Domain model mappers
│   └── repository/         # Repository implementations
├── domain/
│   ├── model/              # Pure domain models (WeatherData, GeocodingResult)
│   └── usecase/            # Business logic (SearchCity, GetWeather, SaveReport, etc.)
├── ui/
│   ├── weather/            # WeatherScreen, WeatherViewModel, WeatherUiState
│   ├── create_report/      # CreateReportScreen, CreateReportViewModel
│   ├── camera/             # CameraScreen, CameraViewModel (CameraX)
│   ├── reports/            # SavedReportsScreen, SavedReportsViewModel
│   └── theme/              # Material3 theme, typography, color tokens
├── navigation/             # NavHost, Screen definitions, animated transitions
├── di/                     # Hilt modules (DatabaseModule, NetworkModule)
└── util/                   # ImageCompressor, WeatherCodeUtil
```

---

## 📋 Evaluation Criteria Fulfillment

### 1. MVVM Architecture & Code Organization (18%)

- **Clean Architecture**: Three distinct layers — `data`, `domain`, `ui` — with unidirectional data flow.
- **Domain Use Cases**: All business logic is encapsulated in dedicated Use Case classes (`SearchCityUseCase`, `GetWeatherUseCase`, `SaveWeatherReportUseCase`, `DeleteWeatherReportUseCase`, `GetAllWeatherReportsUseCase`). ViewModels depend only on Use Cases, never directly on Repositories.
- **Reactive State**: All UI state is managed via `StateFlow` with sealed class hierarchies (`Idle`, `Loading`, `Success`, `Error`), collected lifecycle-safely using `collectAsStateWithLifecycle()`.
- **Dependency Injection**: Hilt provides all dependencies. `@Singleton` scoped repositories, `@HiltViewModel` annotated ViewModels. Two Hilt modules: `DatabaseModule` (Room) and `NetworkModule` (Retrofit + OkHttp).
- **Shared ViewModel Scoping**: `CreateReportScreen` retrieves `WeatherViewModel` from the parent back-stack entry via `navController.getBackStackEntry()`, ensuring the selected weather snapshot is shared without global state.

### 2. Compose UI Quality & Responsiveness (17%)

- **Premium Dark Theme**: Custom olive-dark color palette (`#1E211A`, `#2D3228`, `#C2D68C`) with gradient header cards and tinted stat badges.
- **Full Scrollability**: All screens use `verticalScroll(rememberScrollState())` or `LazyColumn` to support any screen size. 32dp bottom spacers ensure content is accessible above system navigation.
- **Animated Transitions**: `AnimatedContent` for save button states, `AnimatedVisibility` for search suggestions, and smooth slide-in/slide-out navigation transitions (400ms with combined fade).
- **Responsive Cards**: Weather stat badges with color-coded backgrounds (teal for humidity, blue for wind, green for pressure). Image preview cards with compression metadata display.

### 3. API Integration, Loading/Error States & Caching (14%)

- **Open-Meteo API**: Two separate Retrofit services — `GeocodingApiService` for city search and `WeatherApiService` for weather data. No API key required.
- **Debounced Search**: 400ms debounce on the search query via `debounce()` + `distinctUntilChanged()` in `WeatherViewModel`, preventing excessive API calls.
- **State Machine**: Every network operation transitions through `Idle → Loading → Success/Error`. Errors display localized messages with retry capability.
- **DTO → Domain Mapping**: Dedicated mapper classes (`GeocodingMapper`, `WeatherMapper`, `ReportMapper`) convert raw API responses and DB entities to clean domain models, isolating the UI from data-layer changes.

### 4. CameraX Custom Camera Implementation (14%)

- **Custom Overlay**: Fully custom camera UI built with Compose, integrating `PreviewView` via `AndroidView` for the camera feed.
- **ImageCapture Pipeline**: `ImageCapture.takePicture()` → EXIF-corrected bitmap → JPEG compression → persisted to `context.filesDir`.
- **Lifecycle Binding**: Camera use case (`Preview`, `ImageCapture`) bound to `LifecycleOwner` via `ProcessCameraProvider`, ensuring correct teardown on navigation away.
- **Permission Handling**: Runtime permission request with graceful fallback UI (message + back button) when camera permission is denied.

### 5. Image Compression & File Handling (10%)

- **JPEG Compression**: Custom `ImageCompressor` utility that compresses at 80% quality on `Dispatchers.IO`.
- **EXIF Rotation**: Reads `ExifInterface.TAG_ORIENTATION` and rotates the bitmap accordingly before compression — prevents rotated photos on Samsung/Pixel devices.
- **Size Metadata**: Both original and compressed sizes are tracked and displayed in the UI with a computed savings percentage.
- **No File Leaks**:
  - When a user retakes a photo, the previous temp file is immediately deleted (`onCameraResultReceived()`).
  - When a saved report is deleted from Room, its image file is deleted from disk (`WeatherReportRepository.deleteReport()`).
  - When a draft is discarded or a new city is selected, the orphaned image file is cleaned up.
- **Bitmap Recycling**: Explicit `bitmap.recycle()` calls after compression to prevent `OutOfMemoryError` on lower-end devices.

### 6. Room DB with IO-Thread Usage (9%)

- **Database**: `WeatherSnapDatabase` (Room) with two entities:
  - `WeatherReportEntity` — Saved reports with auto-generated primary key.
  - `ReportDraftEntity` — Singleton draft (PK = 1, `OnConflictStrategy.REPLACE`).
- **Reactive Queries**: `WeatherReportDao.getAllReports()` returns `Flow<List<WeatherReportEntity>>`, enabling real-time UI updates when reports are added or deleted.
- **IO Safety**: Every repository method wraps database calls in `withContext(Dispatchers.IO)`. DAO methods are `suspend` functions.
- **Migration Strategy**: `fallbackToDestructiveMigration()` — acceptable because the draft table is ephemeral and saved reports are user-generated content that is unlikely to be affected by schema changes in early development.

### 7. Developer Judgment Challenge (10%)

**Problem**: If the user selects weather → opens Create Report → captures a photo → enters notes → rotates the device or backgrounds the app before saving, the in-progress report must be recoverable without creating duplicate saved reports.

**Solution — Room-Backed Singleton Draft**:

The `CreateReportViewModel` persists the complete in-progress report to a Room `report_drafts` table after every change (notes typed, photo captured). This table uses a **singleton pattern** (fixed `id = 1` with `OnConflictStrategy.REPLACE`), ensuring only one draft exists at a time.

| Scenario | Behavior |
|:---|:---|
| **Device rotation** | ViewModel survives config change; draft is also in Room as a safety net |
| **Process death** | On re-entry, `initWithWeather()` finds the draft in Room and restores the **exact weather snapshot** (including `weatherCode` for icons), notes, and image path |
| **Same city, resume** | Draft is fully restored — zero data loss |
| **Different city selected** | Old draft's image file is **deleted from disk**, draft is cleared in Room, fresh draft is created for the new city |
| **Report saved successfully** | Draft is cleared from Room; image ownership transfers to the saved report |
| **Draft explicitly discarded** | `discardDraft()` deletes the temp image file and clears the Room entry |

**Why this approach?**

- **Not `SavedStateHandle`**: `SavedStateHandle` can't hold large image paths reliably across process death, and can't survive full app kills. Room is durable.
- **Not `onSaveInstanceState`**: Same limitations as `SavedStateHandle`, plus Compose doesn't use it naturally.
- **Not `ViewModel` alone**: ViewModels survive config changes but not process death. Room survives everything.

**Trade-offs**:
- Minor write overhead (Room `INSERT OR REPLACE` on each keystroke), mitigated by Room's efficient single-row operations.
- Draft cleanup relies on explicit lifecycle hooks (`saveReport()`, `discardDraft()`, `initWithWeather()` on city switch) rather than automated garbage collection.

**Key implementation files**:
- [`CreateReportViewModel.kt`](app/src/main/java/com/weathersnap/app/ui/create_report/CreateReportViewModel.kt) — Draft persistence logic
- [`ReportDraftEntity.kt`](app/src/main/java/com/weathersnap/app/data/local/entity/ReportDraftEntity.kt) — Singleton Room entity
- [`ReportDraftDao.kt`](app/src/main/java/com/weathersnap/app/data/local/dao/ReportDraftDao.kt) — DAO with `INSERT OR REPLACE`
- [`ReportDraftRepository.kt`](app/src/main/java/com/weathersnap/app/data/repository/ReportDraftRepository.kt) — IO-safe repository wrapper

### 8. Navigation, Animations & UX Polish (4%)

- **Type-Safe Navigation**: `Screen` sealed class with route constants. `WeatherSnapNavHost` handles all routing with `NavHost`.
- **Animated Transitions**: Global slide + fade transitions (400ms) on `enterTransition`, `exitTransition`, `popEnterTransition`, `popExitTransition`.
- **Back-Stack Sharing**: `CreateReportScreen` and `CameraScreen` share ViewModels via `navController.getBackStackEntry()`, eliminating redundant API calls.
- **Micro-Animations**: `AnimatedContent` on the Save button (idle/saving/error states), `AnimatedVisibility` on search suggestions dropdown.

### 9. Code Quality, Naming & Readability (4%)

- **Consistent Naming**: Screens end in `Screen`, ViewModels in `ViewModel`, states in `UiState`. Entities end in `Entity`, DAOs in `Dao`.
- **Single Responsibility**: Each file has one clear purpose. Use Cases contain one business operation each.
- **Documentation**: Key decisions documented in-code (draft recovery logic, image cleanup rationale).
- **Unit Tests**: `WeatherViewModelTest` validates state transitions using `MockK`, `Turbine`, and `kotlinx-coroutines-test` with `StandardTestDispatcher`.

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|:---|:---|:---|
| Language | Kotlin | 2.0.21 |
| UI | Jetpack Compose (Material 3) | BOM 2024.09.03 |
| DI | Hilt | 2.52 |
| Database | Room | 2.6.1 |
| Networking | Retrofit + OkHttp | 2.11.0 / 4.12.0 |
| Camera | CameraX | 1.3.4 |
| Image Loading | Coil | 2.7.0 |
| Navigation | Navigation Compose | 2.8.3 |
| Async | Kotlin Coroutines + Flow | 1.9.0 |
| Testing | MockK, Turbine, Coroutines Test | 1.13.12 / 1.1.0 |

---

## 🚀 Getting Started

1. **Clone** the repository:
   ```bash
   git clone https://github.com/Vikash9546/WeatherSnap.git
   ```
2. **Open** in Android Studio (Ladybug or newer).
3. **Build and Run** on a physical device (CameraX requires a real camera):
   ```bash
   ./gradlew installDebug
   ```
4. **Run Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

> **Note**: No API key is required. WeatherSnap uses the free [Open-Meteo API](https://open-meteo.com/) for weather data and geocoding.

---

## 📄 License

This project is for educational and evaluation purposes.
