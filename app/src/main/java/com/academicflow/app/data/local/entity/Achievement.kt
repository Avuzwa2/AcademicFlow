package com.academicflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A badge earned by the user (or in progress).
 * Examples: "7-Day Streak", "Subject Master", "Early Riser".
 */
@Entity(
    tableName = "achievements",
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
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val achievementId: Long = 0L,

    val userId: Long,

    // e.g. "7-Day Streak!"
    val badgeName: String,

    // Short description of what unlocks it
    val badgeDescription: String,

    // 0-100 (100 means earned)
    val progressPercentage: Int = 0,

    // When the badge was earned (null if still in progress)
    val earnedDate: Long? = null,

    val createdAt: Long = System.currentTimeMillis()
)