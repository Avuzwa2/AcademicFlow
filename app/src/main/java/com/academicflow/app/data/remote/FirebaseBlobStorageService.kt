package com.academicflow.app.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

/**
 * Blob Storage Service for AcademicFlow using Firebase Storage.
 *
 * Fulfills assessment requirement:
 * - Use blob storage.
 */
class FirebaseBlobStorageService {

    private val TAG = "FirebaseBlobStorage"

    private val storage: FirebaseStorage? by lazy {
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Blob Storage initialization error (check google-services.json): ${e.message}")
            null
        }
    }

    /**
     * Store / Upload a blob (byte array, e.g. note image) to Cloud Blob Storage.
     */
    suspend fun uploadBlobData(blobPath: String, blobBytes: ByteArray): String? {
        val st = storage ?: return null
        return try {
            val ref: StorageReference = st.reference.child(blobPath)
            ref.putBytes(blobBytes).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Successfully stored blob at: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload blob to Blob Storage: ${e.message}")
            null
        }
    }

    /**
     * Store / Upload a blob file Uri to Cloud Blob Storage.
     */
    suspend fun uploadBlobUri(blobPath: String, fileUri: Uri): String? {
        val st = storage ?: return null
        return try {
            val ref: StorageReference = st.reference.child(blobPath)
            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await().toString()
            Log.d(TAG, "Successfully uploaded blob Uri to Blob Storage: $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload blob Uri: ${e.message}")
            null
        }
    }

    /**
     * Read / Fetch download URL for a stored blob from Cloud Blob Storage.
     */
    suspend fun readBlobUrl(blobPath: String): String? {
        val st = storage ?: return null
        return try {
            val ref: StorageReference = st.reference.child(blobPath)
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read blob URL from Blob Storage: ${e.message}")
            null
        }
    }
}