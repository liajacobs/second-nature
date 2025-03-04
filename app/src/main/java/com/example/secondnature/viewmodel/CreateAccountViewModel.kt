package com.example.secondnature.viewmodel

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
        return authRepository.createAccount(firstName, lastName, email, username, password)
    }
}
