package com.academicflow.app.data.remote

import android.util.Log
import com.academicflow.app.data.local.entity.StudySession
import com.academicflow.app.data.local.entity.Subject
import com.academicflow.app.data.local.entity.User
import com.academicflow.app.data.local.entity.WeeklyGoal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * NoSQL Database Service for AcademicFlow using Firebase Cloud Firestore.
 * 
 * Fulfills assessment requirements:
 * - Use a NoSQL database to store data.
 * - Use a NoSQL database to read data.
 */
class FirebaseNoSqlService {

    private val TAG = "FirebaseNoSqlService"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Firestore initialization error (check google-services.json): ${e.message}")
            null
        }
    }

    // ==========================================
    // STORE DATA (NoSQL Database)
    // ==========================================

    suspend fun storeUserNoSql(user: User): Boolean {
        val db = firestore ?: return false
        return try {
            val userMap = hashMapOf(
                "userId" to user.userId,
                "username" to user.username,
                "passwordHash" to user.passwordHash,
                "createdAt" to user.createdAt
            )
            db.collection("users")
                .document(user.userId.toString())
                .set(userMap)
                .await()
            Log.d(TAG, "Stored User document in NoSQL DB: ${user.username}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store user in NoSQL DB: ${e.message}")
            false
        }
    }

    suspend fun storeSubjectNoSql(subject: Subject): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = if (subject.subjectId != 0L) subject.subjectId.toString() else System.currentTimeMillis().toString()
            val subjectMap = hashMapOf(
                "subjectId" to subject.subjectId,
                "userId" to subject.userId,
                "subjectName" to subject.subjectName,
                "colorCode" to subject.colorCode
            )
            db.collection("subjects")
                .document(docId)
                .set(subjectMap)
                .await()
            Log.d(TAG, "Stored Subject document in NoSQL DB: ${subject.subjectName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store subject in NoSQL DB: ${e.message}")
            false
        }
    }

    suspend fun storeStudySessionNoSql(session: StudySession): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = if (session.sessionId != 0L) session.sessionId.toString() else System.currentTimeMillis().toString()
            val sessionMap = hashMapOf(
                "sessionId" to session.sessionId,
                "userId" to session.userId,
                "subjectId" to session.subjectId,
                "durationHours" to session.durationHours,
                "sessionDate" to session.sessionDate,
                "description" to session.description,
                "photoUri" to session.photoUri,
                "createdAt" to session.createdAt
            )
            db.collection("study_sessions")
                .document(docId)
                .set(sessionMap)
                .await()
            Log.d(TAG, "Stored StudySession document in NoSQL DB: $docId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store study session in NoSQL DB: ${e.message}")
            false
        }
    }

    suspend fun storeWeeklyGoalNoSql(goal: WeeklyGoal): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = if (goal.goalId != 0L) goal.goalId.toString() else "${goal.userId}_${goal.weekStartDate}"
            val goalMap = hashMapOf(
                "goalId" to goal.goalId,
                "userId" to goal.userId,
                "weekStartDate" to goal.weekStartDate,
                "totalHoursGoal" to goal.totalHoursGoal,
                "subjectGoals" to goal.subjectGoals
            )
            db.collection("weekly_goals")
                .document(docId)
                .set(goalMap)
                .await()
            Log.d(TAG, "Stored WeeklyGoal document in NoSQL DB: $docId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store weekly goal in NoSQL DB: ${e.message}")
            false
        }
    }

    // ==========================================
    // READ DATA (NoSQL Database)
    // ==========================================

    suspend fun readUserNoSql(userId: Long): Map<String, Any>? {
        val db = firestore ?: return null
        return try {
            val snapshot = db.collection("users")
                .document(userId.toString())
                .get()
                .await()
            if (snapshot.exists()) snapshot.data else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read user from NoSQL DB: ${e.message}")
            null
        }
    }

    suspend fun readSubjectsNoSql(userId: Long): List<Map<String, Any>> {
        val db = firestore ?: return emptyList()
        return try {
            val snapshot = db.collection("subjects")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read subjects from NoSQL DB: ${e.message}")
            emptyList()
        }
    }

    suspend fun readStudySessionsNoSql(userId: Long): List<Map<String, Any>> {
        val db = firestore ?: return emptyList()
        return try {
            val snapshot = db.collection("study_sessions")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.data }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read study sessions from NoSQL DB: ${e.message}")
            emptyList()
        }
    }

    suspend fun readWeeklyGoalNoSql(userId: Long, weekStartDate: Long): Map<String, Any>? {
        val db = firestore ?: return null
        return try {
            val snapshot = db.collection("weekly_goals")
                .whereEqualTo("userId", userId)
                .whereEqualTo("weekStartDate", weekStartDate)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.data
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read weekly goal from NoSQL DB: ${e.message}")
            null
        }
    }
}