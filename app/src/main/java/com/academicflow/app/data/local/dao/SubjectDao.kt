package com.academicflow.app.data.local.dao

import androidx.room.*
import com.academicflow.app.data.local.entity.Subject
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the subjects table.
 */
@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY subjectName ASC")
    fun observeSubjectsForUser(userId: Long): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY subjectName ASC")
    suspend fun getSubjectsForUser(userId: Long): List<Subject>

    @Query("SELECT * FROM subjects WHERE subjectId = :subjectId LIMIT 1")
    suspend fun getSubjectById(subjectId: Long): Subject?
}