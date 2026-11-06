package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.WeeklyGoalDao
import com.academicflow.app.data.local.entity.WeeklyGoal
import com.academicflow.app.data.remote.FirebaseNoSqlService
import com.academicflow.app.util.DateUtils
import kotlinx.coroutines.flow.Flow

/**
 * Manages weekly study goals.
 * Each user has at most one goal record per week.
 * Data is synced with NoSQL Cloud Database.
 */
class WeeklyGoalRepository(
    private val goalDao: WeeklyGoalDao,
    private val noSqlService: FirebaseNoSqlService? = null
) {

    suspend fun getGoalForCurrentWeek(userId: Long): WeeklyGoal? {
        val weekStart = DateUtils.getStartOfWeek(System.currentTimeMillis())
        return goalDao.getGoalForWeek(userId, weekStart)
    }

    fun observeGoalForCurrentWeek(userId: Long): Flow<WeeklyGoal?> {
        val weekStart = DateUtils.getStartOfWeek(System.currentTimeMillis())
        return goalDao.observeGoalForWeek(userId, weekStart)
    }

    /** Creates or updates the goal for the current week. */
    suspend fun saveGoal(userId: Long, totalHours: Float, subjectGoalsJson: String): Long {
        val weekStart = DateUtils.getStartOfWeek(System.currentTimeMillis())
        val existing = goalDao.getGoalForWeek(userId, weekStart)

        val savedGoal = if (existing != null) {
            val updated = existing.copy(totalHoursGoal = totalHours, subjectGoals = subjectGoalsJson)
            goalDao.updateGoal(updated)
            updated
        } else {
            val newGoal = WeeklyGoal(
                userId = userId,
                weekStartDate = weekStart,
                totalHoursGoal = totalHours,
                subjectGoals = subjectGoalsJson
            )
            val id = goalDao.insertGoal(newGoal)
            newGoal.copy(goalId = id)
        }

        // Store weekly goal document in NoSQL database
        noSqlService?.storeWeeklyGoalNoSql(savedGoal)

        return savedGoal.goalId
    }
}