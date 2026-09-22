package com.academicflow.app.data.local.dao

import androidx.room.*
import com.academicflow.app.data.local.entity.Achievement
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the achievements table.
 */
@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY progressPercentage DESC")
    fun observeAchievements(userId: Long): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE userId = :userId AND badgeName = :badgeName LIMIT 1")
    suspend fun getAchievementByName(userId: Long, badgeName: String): Achievement?

    @Query("SELECT COUNT(*) FROM achievements WHERE userId = :userId AND earnedDate IS NOT NULL")
    suspend fun getEarnedBadgeCount(userId: Long): Int
}