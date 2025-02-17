package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d("LoginViewModel", "Login successful: ${result.user?.email}")
            true
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Login failed: ${e.message}")
            false
        }
    }
}
