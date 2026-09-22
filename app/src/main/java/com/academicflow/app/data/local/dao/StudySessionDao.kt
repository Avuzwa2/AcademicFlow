package com.academicflow.app.data.local.dao

import androidx.room.*
import com.academicflow.app.data.local.entity.StudySession
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the study_sessions table.
 * Includes queries used by the history, dashboard, and graph screens.
 */
@Dao
interface StudySessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long

    @Update
    suspend fun updateSession(session: StudySession)

    @Delete
    suspend fun deleteSession(session: StudySession)

    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY sessionDate DESC")
    fun observeAllSessions(userId: Long): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions WHERE userId = :userId AND sessionDate BETWEEN :start AND :end ORDER BY sessionDate DESC")
    fun observeSessionsInRange(userId: Long, start: Long, end: Long): Flow<List<StudySession>>

    @Query("SELECT COALESCE(SUM(durationHours), 0) FROM study_sessions WHERE userId = :userId AND sessionDate BETWEEN :start AND :end")
    suspend fun getTotalHoursInRange(userId: Long, start: Long, end: Long): Float

    @Query("SELECT COALESCE(SUM(durationHours), 0) FROM study_sessions WHERE userId = :userId AND subjectId = :subjectId AND sessionDate BETWEEN :start AND :end")
    suspend fun getHoursForSubjectInRange(userId: Long, subjectId: Long, start: Long, end: Long): Float
}