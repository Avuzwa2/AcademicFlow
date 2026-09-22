package com.academicflow.app.data.local.dao

import androidx.room.*
import com.academicflow.app.data.local.entity.WeeklyGoal
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the weekly_goals table.
 */
@Dao
interface WeeklyGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: WeeklyGoal): Long

    @Update
    suspend fun updateGoal(goal: WeeklyGoal)

    @Query("SELECT * FROM weekly_goals WHERE userId = :userId AND weekStartDate = :weekStart LIMIT 1")
    suspend fun getGoalForWeek(userId: Long, weekStart: Long): WeeklyGoal?

    @Query("SELECT * FROM weekly_goals WHERE userId = :userId AND weekStartDate = :weekStart LIMIT 1")
    fun observeGoalForWeek(userId: Long, weekStart: Long): Flow<WeeklyGoal?>

    @Query("SELECT * FROM weekly_goals WHERE userId = :userId ORDER BY weekStartDate DESC LIMIT 1")
    suspend fun getLatestGoal(userId: Long): WeeklyGoal?
}