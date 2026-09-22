package com.academicflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A study subject (e.g. Open Source Coding, Software Quality and Testing).
 * Each subject belongs to a user.
 */
@Entity(
    tableName = "subjects",
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
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val subjectId: Long = 0L,

    val userId: Long,

    val subjectName: String,

    // Hex colour code used in the UI (e.g. "#4F46E5")
    val colorCode: String = "#4F46E5",

    val createdAt: Long = System.currentTimeMillis()
)