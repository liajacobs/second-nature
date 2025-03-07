package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.secondnature.data.repository.AuthRepository

class CreateAccountViewModel(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun createAccount(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String
    ): Boolean {
        Log.d("CreateAccountViewModel", "Attempting to create account for email: $email, username: $username")
        return authRepository.createAccount(firstName, lastName, email, username, password).also { success ->
            if (success) {
                Log.d("CreateAccountViewModel", "Account creation successful for username: $username")
            } else {
                Log.e("CreateAccountViewModel", "Account creation failed for username: $username")
            }
        }
    }
}
