package com.academicflow.app.util

/**
 * App-wide constant values.
 */
object Constants {

    // Predefined subjects shown during first launch
    val DEFAULT_SUBJECTS = listOf(
        "Open Source Coding" to "#4F46E5",
        "Software Quality and Testing" to "#F59E0B",
        "Database Design" to "#10B981",
        "Communication" to "#EF4444"
    )

    // Achievement badge names
    const val BADGE_STREAK_7 = "7-Day Streak!"
    const val BADGE_SUBJECT_MASTER = "Subject Master"
    const val BADGE_EARLY_RISER = "Early Riser"
    const val BADGE_STREAK_14 = "14-Day Streak"

    // Validation
    const val MIN_USERNAME_LENGTH = 3
    const val MIN_PASSWORD_LENGTH = 6
}