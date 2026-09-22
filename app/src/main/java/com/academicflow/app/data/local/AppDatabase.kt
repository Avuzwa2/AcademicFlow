package com.academicflow.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.academicflow.app.data.local.dao.*
import com.academicflow.app.data.local.entity.*

/**
 * Main Room database for AcademicFlow.
 *
 * Contains all tables and exposes DAOs to the rest of the app.
 * Uses a singleton pattern so only one instance exists across the app.
 */
@Database(
    entities = [
        User::class,
        Subject::class,
        StudySession::class,
        WeeklyGoal::class,
        Achievement::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun subjectDao(): SubjectDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun weeklyGoalDao(): WeeklyGoalDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "academicflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}