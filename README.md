# AcademicFlow 

> **A mobile study tracker app for students — track sessions, set weekly goals, and build better study habits.**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-24%2B-blue.svg)](https://developer.android.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

##  About the App

**AcademicFlow** is a study management application built for students who want to take control of their academic workload. It helps you log study sessions, set weekly goals per subject, visualise your progress through graphs, and stay motivated with achievement badges.

This app was developed as part of **OPSC6311 – Open Source Coding (Part 2)** at **The Independent Institute of Education (IIE)**.

### The Problem It Solves

Most students struggle to track how much time they actually spend studying each subject. Without proper monitoring, it's easy to neglect tougher modules, underestimate your workload, and burn out before exams. AcademicFlow solves this by making study tracking **simple, visual, and even a little fun**.

### Inspiration & Research

AcademicFlow was designed after analysing three existing study/productivity apps:

| App | What We Learned |
| :--- | :--- |
| **Forest** | Visual progress is deeply motivating — we adapted it into a progress bar |
| **YPT (Yeolpumta)** | Subject-based analytics are powerful — we simplified the UI |
| **Study Bunny** | Gamification keeps users engaged — we made badges academic-focused |

Full research and comparative analysis is documented in the **Part 1 planning document**.

---

##  Features

### Core Features
-  **User Registration & Login** — passwords are hashed with SHA-256 before storage
-  **Subject Management** — create, edit, and delete study subjects
- ️ **Study Session Logging** — log duration, subject, date, description, and attach a photo of your notes
-  **Session History** — view past sessions with date filtering
-  **Weekly Goals** — set a total weekly target and per-subject targets
-  **Study Graphs** — bar charts and daily trend lines powered by MPAndroidChart
-  **Achievements** — earn badges like "7-Day Streak" and "Subject Master"
-  **Settings Menu** — profile info, logout, and app preferences

### Technical Features
-  **Offline-first** — all data stored locally using Room (SQLite)
-  **Encrypted passwords** — never stored in plain text
-  **REST API integration** — cloud backup endpoint (coming in Part 3)
-  **Material Design 3** — clean, modern UI following Google's guidelines
-  **MVVM Architecture** — separation of concerns for maintainability

---

##  Design Considerations

### Architecture

AcademicFlow follows the **MVVM (Model-View-ViewModel)** pattern:


**Why MVVM?**
- **Testable** — ViewModels can be unit tested without UI
- **Scalable** — adding new features doesn't break existing code
- **Lifecycle-aware** — survives screen rotations cleanly

### Package Structure


### UI/UX Principles

- **Consistent colour palette** — primary `#4F46E5` (indigo), accent `#7C3AED` (purple)
- **Material Design components** — `TextInputLayout`, `MaterialButton`, `MaterialCardView`
- **Simple navigation** — all main screens reachable from the Dashboard
- **Clear feedback** — progress bars during loading, Toasts for errors

### Security

- **SHA-256 password hashing** — no plain text passwords ever stored
- **Local-only by default** — user data stays on-device unless they opt into cloud backup
- **No hardcoded API keys** — sensitive config stored outside version control

---

##  Tech Stack

| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Language** | Kotlin | Modern Android development |
| **UI** | XML Layouts + Material 3 | Native Android UI |
| **Architecture** | MVVM + Repository | Clean separation of concerns |
| **Local DB** | Room (SQLite) | Persistent storage |
| **Async** | Kotlin Coroutines + Flow | Non-blocking operations |
| **Networking** | Retrofit + OkHttp | REST API communication |
| **Image Loading** | Glide | Efficient image handling |
| **Charts** | MPAndroidChart | Study graphs |
| **Security** | AndroidX Security Crypto | Encrypted credentials |
| **Build** | Gradle Kotlin DSL | Build automation |
| **CI/CD** | GitHub Actions | Automated build + test |

---

##  Getting Started

### Prerequisites
- **Android Studio** Panda 4 (2025.3.4) or newer
- **JDK** 17+
- **Android SDK** API 24+ (compile SDK 36)
- **Git** installed

### Installation

1. **Clone the repo**
   ```bash
   git clone https://github.com/Avuzwa2/AcademicFlow.git
   cd AcademicFlow