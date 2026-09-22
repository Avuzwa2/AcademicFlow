package com.academicflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single logged study session.
 * Links a user to a subject and records how long they studied.
 */
@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["subjectId"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId"), Index("subjectId")]
)
data class StudySession(
    @PrimaryKey(autoGenerate = true)
    val sessionId: Long = 0L,

    val userId: Long,

    val subjectId: Long,

    // Duration in hours (e.g. 1.5)
    val durationHours: Float,

    // Date the session took place (millis since epoch)
    val sessionDate: Long,

    val description: String = "",

    // Local file path to attached photo (if any)
    val photoUri: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)