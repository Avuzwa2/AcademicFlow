package com.academicflow.app.data.repository

import android.net.Uri
import com.academicflow.app.data.local.dao.StudySessionDao
import com.academicflow.app.data.local.entity.StudySession
import com.academicflow.app.data.remote.FirebaseBlobStorageService
import com.academicflow.app.data.remote.FirebaseNoSqlService
import com.academicflow.app.util.DateUtils
import kotlinx.coroutines.flow.Flow

/**
 * Handles study session logging and retrieval.
 * Data is stored in Room and synced with NoSQL Cloud Database & Cloud Blob Storage.
 */
class StudySessionRepository(
    private val sessionDao: StudySessionDao,
    private val noSqlService: FirebaseNoSqlService? = null,
    private val blobStorageService: FirebaseBlobStorageService? = null
) {

    fun observeAllSessions(userId: Long): Flow<List<StudySession>> =
        sessionDao.observeAllSessions(userId)

    fun observeSessionsInRange(userId: Long, start: Long, end: Long): Flow<List<StudySession>> =
        sessionDao.observeSessionsInRange(userId, start, end)

    suspend fun addSession(session: StudySession): Long {
        var cloudPhotoUrl = session.photoUri
        
        // If local photo URI attached, upload to Blob Storage
        if (!session.photoUri.isNullOrEmpty() && session.photoUri.startsWith("content://")) {
            try {
                val blobPath = "session_photos/${session.userId}/${System.currentTimeMillis()}.jpg"
                val uploadedUrl = blobStorageService?.uploadBlobUri(blobPath, Uri.parse(session.photoUri))
                if (uploadedUrl != null) {
                    cloudPhotoUrl = uploadedUrl
                }
            } catch (e: Exception) {
                // Fallback to local photo URI if cloud upload skipped
            }
        }

        val sessionToSave = session.copy(photoUri = cloudPhotoUrl)
        val id = sessionDao.insertSession(sessionToSave)
        val finalSession = sessionToSave.copy(sessionId = id)

        // Store study session document in NoSQL Database
        noSqlService?.storeStudySessionNoSql(finalSession)

        return id
    }

    suspend fun updateSession(session: StudySession) {
        sessionDao.updateSession(session)
        noSqlService?.storeStudySessionNoSql(session)
    }

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