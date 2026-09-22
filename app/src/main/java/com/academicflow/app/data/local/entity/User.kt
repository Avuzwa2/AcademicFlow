package com.academicflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user of the AcademicFlow app.
 * Stores credentials and profile information locally.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L,

    val username: String,

    // Store the SHA-256 hash of the password, never the plain text
    val passwordHash: String,

    val createdAt: Long = System.currentTimeMillis(),

    // Optional settings
    val dailyReminderEnabled: Boolean = false,
    val weeklyGoalHours: Float = 20f
)