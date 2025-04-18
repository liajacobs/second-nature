package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getUserProfile(): Result<User> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val document = firestore.collection("users").document(userId).get().await()
            val user =
                    document.toObject(User::class.java)
                            ?: throw Exception("Failed to retrieve user data")

            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user profile: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(
            firstName: String,
            lastName: String,
            username: String
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

            // Check if username is already taken by another user
            val usernameQuery =
                    firestore.collection("users").whereEqualTo("username", username).get().await()

            if (!usernameQuery.isEmpty && usernameQuery.documents[0].id != userId) {
                throw Exception("Username already taken")
            }

            // Update user profile
            val updates =
                    hashMapOf<String, Any>(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username
                    )

            firestore.collection("users").document(userId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user profile: ${e.message}")
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
