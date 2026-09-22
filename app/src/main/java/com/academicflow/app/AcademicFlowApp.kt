package com.academicflow.app

import android.app.Application
import com.academicflow.app.data.local.AppDatabase
import com.academicflow.app.data.repository.*

/**
 * Custom Application class.
 *
 * Initialises the Room database once and exposes all repositories
 * to ViewModels. Accessed via:
 *
 *     val app = application as AcademicFlowApp
 *     val repo = app.userRepository
 */
class AcademicFlowApp : Application() {

    // Lazy-initialised database — created on first access
    val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    // Repositories — each one is created once and reused
    val userRepository: UserRepository by lazy {
        UserRepository(database.userDao())
    }

    val subjectRepository: SubjectRepository by lazy {
        SubjectRepository(database.subjectDao())
    }

    val studySessionRepository: StudySessionRepository by lazy {
        StudySessionRepository(database.studySessionDao())
    }

    val weeklyGoalRepository: WeeklyGoalRepository by lazy {
        WeeklyGoalRepository(database.weeklyGoalDao())
    }

    val achievementRepository: AchievementRepository by lazy {
        AchievementRepository(database.achievementDao())
    }
}