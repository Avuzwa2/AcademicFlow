package com.academicflow.app.data.repository

import com.academicflow.app.data.local.dao.UserDao
import com.academicflow.app.data.local.entity.User
import com.academicflow.app.data.remote.FirebaseNoSqlService
import com.academicflow.app.util.PasswordHasher
import kotlinx.coroutines.flow.Flow

/**
 * Handles user-related operations: registration and login.
 * Passwords are hashed before being stored.
 * Data is synced to NoSQL Cloud Database.
 */
class UserRepository(
    private val userDao: UserDao,
    private val noSqlService: FirebaseNoSqlService? = null
) {

    suspend fun register(username: String, password: String): Result<Long> {
        return try {
            if (userDao.usernameExists(username) > 0) {
                return Result.failure(Exception("Username already taken"))
            }
            val hashed = PasswordHasher.hash(password)
            val newUser = User(username = username, passwordHash = hashed)
            val id = userDao.insertUser(newUser)
            
            // Store user in NoSQL database
            noSqlService?.storeUserNoSql(newUser.copy(userId = id))

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(username: String, password: String): Result<User> {
        val user = userDao.getUserByUsername(username)
            ?: return Result.failure(Exception("User not found"))

        return if (PasswordHasher.verify(password, user.passwordHash)) {
            // Also store/sync into NoSQL database on successful login
            noSqlService?.storeUserNoSql(user)
            Result.success(user)
        } else {
            Result.failure(Exception("Incorrect password"))
        }
    }

    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)

    fun observeUser(userId: Long): Flow<User?> = userDao.observeUser(userId)

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
        noSqlService?.storeUserNoSql(user)
    }
}