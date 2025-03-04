package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d("LoginViewModel", "Login successful: ${result.user?.email}")
            result.user != null // Return true if login is successful
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login failed: ${e.message}")
            false // Return false if login fails
        }
    }
    suspend fun createAccount(
            firstName: String,
            lastName: String,
            email: String,
            username: String,
            password: String
    ): Boolean {
        return try {
            // Check if username is already taken
            val usernameQuery =
                    firestore.collection("users").whereEqualTo("username", username).get().await()

            if (!usernameQuery.isEmpty) {
                Log.e("AuthRepository", "Username already taken")
                return false
            }

            // Create user with Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()

            // Retrieve user UID from Firebase Authentication result
            Log.d("AuthRepository", "Auth result: ${authResult.user?.uid}")
            val userId = authResult.user?.uid ?: return false // Return false if userId is null

            // Create a User object without the password field
            val user = User(firstName, lastName, email, username)

            // Store user data in Firestore (without the password)
            val userDocRef = firestore.collection("users").document(userId)

            // Set user data to Firestore document
            userDocRef.set(user).await()

            // Successfully created account and stored user in Firestore
            true
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Handle invalid credentials, like email format errors
            Log.e("AuthRepository", "Invalid email or password: ${e.message}")
            false
        } catch (e: FirebaseAuthUserCollisionException) {
            // Handle email already in use
            Log.e("AuthRepository", "Email already in use: ${e.message}")
            false
        } catch (e: Exception) {
            // Catch any other exceptions
            Log.e("AuthRepository", "Error creating account: ${e.message}")
            false
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            val user =
                    auth.currentUser ?: return Result.failure(Exception("User not authenticated"))
            val email = user.email ?: return Result.failure(Exception("User email not found"))
            val credential = EmailAuthProvider.getCredential(email, password)

            user.reauthenticate(credential).await()

            val userId = user.uid
            firestore.collection("users").document(userId).delete().await()

            user.delete().await()

            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Delete account failed: ${e.message}")
            Result.failure(e)
        }
    }
}
