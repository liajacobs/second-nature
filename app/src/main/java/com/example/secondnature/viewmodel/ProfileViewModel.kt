package com.example.secondnature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.User
import com.example.secondnature.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository
                    .getUserProfile()
                    .onSuccess {
                        _user.value = it
                        _error.value = null
                    }
                    .onFailure { _error.value = "Failed to load profile: ${it.message}" }
            _isLoading.value = false
        }
    }

    fun updateProfile(firstName: String, lastName: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository
                    .updateUserProfile(firstName, lastName, username)
                    .onSuccess {
                        loadUserProfile()
                        _error.value = null
                    }
                    .onFailure { _error.value = "Failed to update profile: ${it.message}" }
            _isLoading.value = false
        }
    }
}
