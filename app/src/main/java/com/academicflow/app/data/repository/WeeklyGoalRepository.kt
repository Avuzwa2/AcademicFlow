package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.WeeklyGoalDao
import com.academicflow.app.data.local.entity.WeeklyGoal
import com.academicflow.app.util.DateUtils
import kotlinx.coroutines.flow.Flow

/**
 * Manages weekly study goals.
 * Each user has at most one goal record per week.
 */
class WeeklyGoalRepository(private val goalDao: WeeklyGoalDao) {

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

        return if (existing != null) {
            goalDao.updateGoal(
                existing.copy(totalHoursGoal = totalHours, subjectGoals = subjectGoalsJson)
            )
            existing.goalId
        } else {
            goalDao.insertGoal(
                WeeklyGoal(
                    userId = userId,
                    weekStartDate = weekStart,
                    totalHoursGoal = totalHours,
                    subjectGoals = subjectGoalsJson
                )
            )
        }
    }
}