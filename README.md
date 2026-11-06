# AcademicFlow 🎓

> **A Comprehensive Android Study Tracking Application with Offline-First Sync, NoSQL Cloud Database, Blob Storage, Multi-Language Support, and Automated CI/CD Testing.**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Build Status](https://github.com/Avuzwa2/AcademicFlow/actions/workflows/build.yml/badge.svg)](https://github.com/Avuzwa2/AcademicFlow/actions/workflows/build.yml)
[![API](https://img.shields.io/badge/API-24%2B-blue.svg)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📱 Executive Overview & App Purpose

**AcademicFlow** is a modern, student-centric academic productivity application designed to empower students to track their study time, manage modules, set weekly goal targets, and stay motivated throughout the semester.

### The Core Problem It Solves
Students frequently struggle with time management across multiple modules, resulting in burnout and neglected subjects. **AcademicFlow** addresses this by providing:
- Real-time time tracking per subject.
- Automated goal progress visualization.
- Multi-language accessibility for South African students (English, Afrikaans, and isiZulu).
- Seamless offline functionality with automatic background cloud synchronization when connected.

---

## 🚀 Release Notes (Updates Since Prototype / Part 3 Additions)

This final release (**v1.0 - PoE Final Version**) builds upon the initial prototype with major architectural upgrades and innovative feature enhancements:

### 🌟 Key Innovative Features Introduced in Version 1.0:
1. **Cloud Blob Storage Integration**:
   - Integrated **Firebase Cloud Storage** for storing image note blobs attached to study sessions.
2. **NoSQL Cloud Database Integration**:
   - Integrated **Google Cloud Firestore** (NoSQL Database) to store and read live user documents, study sessions, weekly goals, and subjects.
3. **Single Sign-On (SSO)**:
   - Added Single Sign-On capabilities allowing 1-tap authenticated access and instant security notifications.
4. **Multi-Language Accessibility (South African Languages)**:
   - Full localized string support for **English**, **Afrikaans** (`values-af`), and **isiZulu** (`values-zu`).
5. **Real-time Push & Alert Notification System**:
   - Implemented `NotificationHelper` utilizing Android Notification Channels to push real-time alerts for study goals and authentication updates.
6. **Live REST API Quote Integration**:
   - Retrofit-powered integration to fetch live study motivation quotes from an online REST API (`ZenQuotes API`).
7. **Offline Mode with Automatic Cloud Sync**:
   - RoomDB persistent local caching paired with auto-sync capability when network connectivity is restored.
8. **Automated CI/CD Pipeline (GitHub Actions)**:
   - Integrated automated testing and APK compilation on every push/PR via GitHub Actions (`.github/workflows/build.yml`).
9. **Google Play Store Publication Readiness**:
   - Enabled **R8 Code Shrinking**, resource optimization, ProGuard rules, versioning, and Android App Bundle (`.aab`) generation.

---

## 🏗️ Design Considerations & Architecture

### Architecture Pattern: MVVM (Model-View-ViewModel)
AcademicFlow strictly adheres to Google's recommended **MVVM Architecture** combined with the Repository Pattern:

```
[ UI Layer (Activities & XML) ] 
             │
             ▼
      [ ViewModel Layer ]
             │
             ▼
     [ Repository Layer ]
       │            │
       ▼            ▼
[ Local RoomDB ]  [ Cloud NoSQL & Blob Storage ]
```

### Key Design Pillars:
- **Offline-First Resilience**: All write operations execute locally in RoomDB immediately so the user experience is instant, with background tasks pushing updates to Cloud Firestore NoSQL DB.
- **Security & Privacy**: User passwords are encrypted with SHA-256 before local or cloud storage.
- **Material Design 3 Principles**: Clean typography, adaptive color indicators for subjects, and rounded progress components.

---

## 🛠️ GitHub & GitHub Actions CI/CD Integration

### Version Control & Tagging
This project is hosted on GitHub at [Avuzwa2/AcademicFlow](https://github.com/Avuzwa2/AcademicFlow).
- **Tag**: `finalPoE` marks the official final release commit.

### Continuous Integration (GitHub Actions)
The workflow file `.github/workflows/build.yml` automates the build and testing process on every code push:
- **Runner**: `ubuntu-latest`
- **JDK Version**: Java 17 (Temurin distribution)
- **Automated Tasks**:
  1. `chmod +x gradlew`
  2. `./gradlew testDebugUnitTest` (Runs all local JUnit unit tests)
  3. `./gradlew assembleDebug` (Verifies full APK build compilation)

---

## 📸 Screenshots & UI Assets

| Screen | Description |
| :--- | :--- |
| **Login & SSO** | Encrypted sign-in, account registration, and SSO authentication |
| **Dashboard** | REST API motivational quote, weekly progress bar, and subject breakdown |
| **Add Session** | Date picker, duration, description, and attached photo blob upload |
| **Session History** | List of logged sessions, subject filtering, and photo preview dialogs |
| **Weekly Goals** | Interactive target inputs with dynamic real-time progress banners |
| **App Settings** | Language selector (English, Afrikaans, isiZulu) and notification toggles |

---

## 🧪 Unit Testing

Unit tests are located in `app/src/test/java/com/academicflow/app/AcademicFlowUnitTest.kt` and cover core logic:
- `PasswordHasher`: Verifies SHA-256 hashing and password verification.
- `DateUtils`: Tests start-of-week calculation (Monday 00:00) and date formatting.

To execute tests locally:
```bash
./gradlew testDebugUnitTest
```

---

## 📦 How to Build for Google Play Store

To generate the production Android App Bundle (`.aab`) for Google Play Console:
```bash
./gradlew bundleRelease
```
The resulting `.aab` file is output to: `app/build/outputs/bundle/release/app-release.aab`.

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
