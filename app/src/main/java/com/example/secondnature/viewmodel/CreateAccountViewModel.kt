package com.example.secondnature.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.example.secondnature.viewmodel.User

class CreateAccountViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun createAccount(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            // Step 1: Create user with Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            // Step 2: Retrieve user UID from Firebase Authentication result
            Log.d("CreateAccountViewModel", "Auth result: ${authResult.user?.uid}")
            val userId = authResult.user?.uid ?: return false// Return false if userId is null

            // Step 3: Create a User object without the password field
            val user = User(firstName, lastName, email)

            // Step 4: Store user data in Firestore (without the password)
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
