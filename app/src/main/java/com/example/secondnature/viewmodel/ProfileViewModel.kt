package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.model.User
import com.example.secondnature.data.repository.PostRepository
import com.example.secondnature.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val postRepository = PostRepository()


    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _userPosts = MutableLiveData<List<Post>?>()
    val userPosts: LiveData<List<Post>?> = _userPosts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    init {
        loadUserProfile()
        loadUserPosts()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Loading user profile")
            _isLoading.value = true
            userRepository
                    .getUserProfile()
                    .onSuccess {
                        Log.d("ProfileViewModel", "Successfully loaded profile for user: ${it.username}")
                        _user.value = it
                        _error.value = null
                    }
                    .onFailure { 
                        Log.e("ProfileViewModel", "Failed to load profile: ${it.message}")
                        _error.value = "Failed to load profile: ${it.message}" 
                    }
            _isLoading.value = false
        }
    }

    private fun loadUserPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            postRepository.getUserPosts()
                .onSuccess {
                    _userPosts.value = it
                    _error.value = null
                }
                .onFailure {
                    _error.value = "Failed to load posts: ${it.message}"
                    Log.e("ProfileViewModel", "Failed to load posts: ${it.message}")
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
                        Log.d("ProfileViewModel", "Successfully updated profile for username: $username")
                        loadUserProfile()
                        _error.value = null
                    }
                    .onFailure { 
                        Log.e("ProfileViewModel", "Failed to update profile for username: $username - ${it.message}")
                        _error.value = "Failed to update profile: ${it.message}" 
                    }
            _isLoading.value = false
        }
    }
}
