package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.SubjectDao
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.util.Constants
import kotlinx.coroutines.flow.Flow

/**
 * Handles CRUD operations for study subjects.
 * On first login, seeds the default subjects.
 */
class SubjectRepository(private val subjectDao: SubjectDao) {

    fun observeSubjects(userId: Long): Flow<List<Subject>> =
        subjectDao.observeSubjectsForUser(userId)

    suspend fun getSubjects(userId: Long): List<Subject> =
        subjectDao.getSubjectsForUser(userId)

    suspend fun addSubject(userId: Long, name: String, color: String): Long {
        return subjectDao.insertSubject(
            Subject(userId = userId, subjectName = name, colorCode = color)
        )
    }

    suspend fun updateSubject(subject: Subject) = subjectDao.updateSubject(subject)

    suspend fun deleteSubject(subject: Subject) = subjectDao.deleteSubject(subject)

    /** Inserts default subjects the first time a user logs in. */
    suspend fun seedDefaultsIfEmpty(userId: Long) {
        val existing = subjectDao.getSubjectsForUser(userId)
        if (existing.isEmpty()) {
            Constants.DEFAULT_SUBJECTS.forEach { (name, color) ->
                subjectDao.insertSubject(
                    Subject(userId = userId, subjectName = name, colorCode = color)
                )
            }
        }
    }
}