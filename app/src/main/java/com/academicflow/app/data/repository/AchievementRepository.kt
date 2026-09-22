package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.AchievementDao
import com.academicflow.app.data.local.entity.Achievement
import com.academicflow.app.util.Constants
import kotlinx.coroutines.flow.Flow

/**
 * Handles badges and gamification.
 * Seeds the initial (unearned) badges on first login, then
 * updates their progress as the user studies.
 */
class AchievementRepository(private val achievementDao: AchievementDao) {

    fun observeAchievements(userId: Long): Flow<List<Achievement>> =
        achievementDao.observeAchievements(userId)

    suspend fun seedDefaultBadges(userId: Long) {
        val defaults = listOf(
            Achievement(
                userId = userId,
                badgeName = Constants.BADGE_STREAK_7,
                badgeDescription = "Studied 7 days in a row"
            ),
            Achievement(
                userId = userId,
                badgeName = Constants.BADGE_SUBJECT_MASTER,
                badgeDescription = "Logged 10+ hours in a single subject"
            ),
            Achievement(
                userId = userId,
                badgeName = Constants.BADGE_EARLY_RISER,
                badgeDescription = "Completed 3 sessions before 8 AM"
            ),
            Achievement(
                userId = userId,
                badgeName = Constants.BADGE_STREAK_14,
                badgeDescription = "Studied 14 days in a row"
            )
        )

        defaults.forEach { badge ->
            if (achievementDao.getAchievementByName(userId, badge.badgeName) == null) {
                achievementDao.insertAchievement(badge)
            }
        }
    }

    suspend fun updateProgress(userId: Long, badgeName: String, progress: Int) {
        val badge = achievementDao.getAchievementByName(userId, badgeName) ?: return
        val updated = badge.copy(
            progressPercentage = progress.coerceIn(0, 100),
            earnedDate = if (progress >= 100) System.currentTimeMillis() else null
        )
        achievementDao.updateAchievement(updated)
    }

    suspend fun getEarnedCount(userId: Long): Int =
        achievementDao.getEarnedBadgeCount(userId)
}