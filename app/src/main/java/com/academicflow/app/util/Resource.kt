package com.academicflow.app.util

/**
 * Wraps the result of an async operation so the UI can
 * display loading, success, or error states cleanly.
 */
sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}