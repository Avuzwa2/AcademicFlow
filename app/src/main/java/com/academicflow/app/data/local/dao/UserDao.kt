package com.academicflow.app.data.local.dao

import androidx.room.*
import com.academicflow.app.data.local.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * Data access object for the users table.
 * Handles registration, login, and profile updates.
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<User?>

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun usernameExists(username: String): Int
}