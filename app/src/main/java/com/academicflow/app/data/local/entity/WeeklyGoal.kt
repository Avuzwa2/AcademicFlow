package com.academicflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A weekly study target set by the user.
 * Tracks both the total goal and individual subject targets (stored as JSON).
 */
@Entity(
    tableName = "weekly_goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class WeeklyGoal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0L,

    val userId: Long,

    // Monday of the target week (millis since epoch)
    val weekStartDate: Long,

    val totalHoursGoal: Float,

    // JSON map of subjectId → targetHours e.g. {"1": 6.0, "2": 5.0}
    val subjectGoals: String = "{}",

    val createdAt: Long = System.currentTimeMillis()
)