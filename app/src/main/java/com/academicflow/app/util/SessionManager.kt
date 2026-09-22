package com.academicflow.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Stores the currently logged-in user's ID locally.
 *
 * This is intentionally minimal — we only need to know
 * which user is active so we can query their data.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLoggedInUser(userId: Long, username: String) {
        prefs.edit()
            .putLong(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    fun isLoggedIn(): Boolean = getUserId() != -1L

    fun logout() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "academicflow_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
    }
}