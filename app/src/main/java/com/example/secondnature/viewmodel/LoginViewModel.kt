package com.example.secondnature.viewmodel
import com.example.secondnature.data.repository.AuthRepository

import android.util.Log
import androidx.lifecycle.ViewModel

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun login(email: String, password: String): Boolean {
        Log.d("LoginViewModel", "Attempting login with identifier: $email")
        return authRepository.login(email, password).also { success ->
            if (success) {
                Log.d("LoginViewModel", "Login successful for identifier: $email")
            } else {
                Log.e("LoginViewModel", "Login failed for identifier: $email")
            }
        }
    }
}
