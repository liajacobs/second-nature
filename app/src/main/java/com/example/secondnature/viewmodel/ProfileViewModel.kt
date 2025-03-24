package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.User
import com.example.secondnature.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()

    val user: LiveData<User?> = userRepository.userProfile

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadUserProfile(false)
    }

    fun loadUserProfile(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Loading user profile")
            _isLoading.value = true
            userRepository
                    .getUserProfile(forceRefresh)
                    .onSuccess {
                        Log.d(
                                "ProfileViewModel",
                                "Successfully loaded profile for user: ${it.username}"
                        )
                        _error.value = null
                    }
                    .onFailure {
                        Log.e("ProfileViewModel", "Failed to load profile: ${it.message}")
                        _error.value = "Failed to load profile: ${it.message}"
                    }
            _isLoading.value = false
        }
    }

    fun updateProfile(firstName: String, lastName: String, username: String) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Updating profile for username: $username")
            _isLoading.value = true
            userRepository
                    .updateUserProfile(firstName, lastName, username)
                    .onSuccess {
                        Log.d(
                                "ProfileViewModel",
                                "Successfully updated profile for username: $username"
                        )
                        loadUserProfile(true)
                        _error.value = null
                    }
                    .onFailure {
                        Log.e(
                                "ProfileViewModel",
                                "Failed to update profile for username: $username - ${it.message}"
                        )
                        _error.value = "Failed to update profile: ${it.message}"
                    }
            _isLoading.value = false
        }
    }
}
