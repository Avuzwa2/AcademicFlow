package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.SubjectDao
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.data.remote.FirebaseNoSqlService
import com.academicflow.app.util.Constants
import kotlinx.coroutines.flow.Flow

/**
 * Handles CRUD operations for study subjects.
 * Data is synced with NoSQL Cloud Database.
 */
class SubjectRepository(
    private val subjectDao: SubjectDao,
    private val noSqlService: FirebaseNoSqlService? = null
) {

    fun observeSubjects(userId: Long): Flow<List<Subject>> =
        subjectDao.observeSubjectsForUser(userId)

    suspend fun getSubjects(userId: Long): List<Subject> =
        subjectDao.getSubjectsForUser(userId)

    suspend fun addSubject(userId: Long, name: String, color: String): Long {
        val newSubject = Subject(userId = userId, subjectName = name, colorCode = color)
        val id = subjectDao.insertSubject(newSubject)
        val subjectWithId = newSubject.copy(subjectId = id)
        noSqlService?.storeSubjectNoSql(subjectWithId)
        return id
    }

    suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject)
        noSqlService?.storeSubjectNoSql(subject)
    }

    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    /** Inserts default subjects the first time a user logs in. */
    suspend fun seedDefaultsIfEmpty(userId: Long) {
        val existing = subjectDao.getSubjectsForUser(userId)
        if (existing.isEmpty()) {
            Constants.DEFAULT_SUBJECTS.forEach { (name, color) ->
                val newSubject = Subject(userId = userId, subjectName = name, colorCode = color)
                val id = subjectDao.insertSubject(newSubject)
                noSqlService?.storeSubjectNoSql(newSubject.copy(subjectId = id))
            }
        }
    }
}