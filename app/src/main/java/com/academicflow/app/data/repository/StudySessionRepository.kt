package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.StudySessionDao
import com.academicflow.app.data.local.entity.StudySession
import com.academicflow.app.util.DateUtils
import kotlinx.coroutines.flow.Flow

/**
 * Handles study session logging and retrieval.
 * Also provides summary calculations used by the dashboard.
 */
class StudySessionRepository(private val sessionDao: StudySessionDao) {

    fun observeAllSessions(userId: Long): Flow<List<StudySession>> =
        sessionDao.observeAllSessions(userId)

    fun observeSessionsInRange(userId: Long, start: Long, end: Long): Flow<List<StudySession>> =
        sessionDao.observeSessionsInRange(userId, start, end)

    suspend fun addSession(session: StudySession): Long =
        sessionDao.insertSession(session)

    suspend fun updateSession(session: StudySession) =
        sessionDao.updateSession(session)

    suspend fun deleteSession(session: StudySession) =
        sessionDao.deleteSession(session)

    /** Total hours studied by [userId] in the week containing [referenceMillis]. */
    suspend fun getHoursThisWeek(userId: Long, referenceMillis: Long = System.currentTimeMillis()): Float {
        val start = DateUtils.getStartOfWeek(referenceMillis)
        val end = DateUtils.getEndOfWeek(referenceMillis)
        return sessionDao.getTotalHoursInRange(userId, start, end)
    }

    /** Total hours studied in a specific subject this week. */
    suspend fun getSubjectHoursThisWeek(
        userId: Long,
        subjectId: Long,
        referenceMillis: Long = System.currentTimeMillis()
    ): Float {
        val start = DateUtils.getStartOfWeek(referenceMillis)
        val end = DateUtils.getEndOfWeek(referenceMillis)
        return sessionDao.getHoursForSubjectInRange(userId, subjectId, start, end)
    }
}