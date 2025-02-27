package com.example.secondnature.viewmodel
import com.example.secondnature.data.repository.AuthRepository


import androidx.lifecycle.ViewModel


class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    suspend fun login(email: String, password: String): Boolean {
        return authRepository.login(email, password)
    }
}
