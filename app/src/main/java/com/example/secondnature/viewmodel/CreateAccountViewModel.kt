package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.secondnature.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CreateAccountViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

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
                Log.e("CreateAccountViewModel", "Username already taken")
                return false
            }

            // Create user with Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Retrieve user UID from Firebase Authentication result
            Log.d("CreateAccountViewModel", "Auth result: ${authResult.user?.uid}")
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
            Log.e("CreateAccountViewModel", "Invalid email or password: ${e.message}")
            false
        } catch (e: FirebaseAuthUserCollisionException) {
            // Handle email already in use
            Log.e("CreateAccountViewModel", "Email already in use: ${e.message}")
            false
        } catch (e: Exception) {
            // Catch any other exceptions
            Log.e("CreateAccountViewModel", "Error creating account: ${e.message}")
            false
        }
    }
}
