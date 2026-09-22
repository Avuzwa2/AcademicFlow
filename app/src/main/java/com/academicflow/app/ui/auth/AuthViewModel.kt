package com.academicflow.app.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.academicflow.app.AcademicFlowApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Handles authentication state for the Login and Registration screens.
 */
class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val appContainer = app as AcademicFlowApp

    // UI state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please enter username and password")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = appContainer.userRepository.login(username.trim(), password)
            result.onSuccess { user ->
                _authState.value = AuthState.LoginSuccess(user.userId, user.username)
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        if (username.length < 3) {
            _authState.value = AuthState.Error("Username must be at least 3 characters")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = appContainer.userRepository.register(username.trim(), password)
            result.onSuccess { userId ->
                // Seed default subjects & badges for new user
                appContainer.subjectRepository.seedDefaultsIfEmpty(userId)
                appContainer.achievementRepository.seedDefaultBadges(userId)

                _authState.value = AuthState.LoginSuccess(userId, username.trim())
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        data object Idle : AuthState()
        data object Loading : AuthState()
        data class LoginSuccess(val userId: Long, val username: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}