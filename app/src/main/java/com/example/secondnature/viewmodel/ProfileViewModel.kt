package com.example.secondnature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                val document = firestore.collection("users").document(userId).get().await()

                _user.value = document.toObject(User::class.java)
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(firstName: String, lastName: String, username: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Check if username is already taken
                val usernameQuery =
                        firestore
                                .collection("users")
                                .whereEqualTo("username", username)
                                .get()
                                .await()

                if (!usernameQuery.isEmpty && usernameQuery.documents[0].id != userId) {
                    throw Exception("Username already taken")
                }

                // Update user profile
                val updates =
                        hashMapOf<String, Any>(
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "username" to username
                        )

                firestore.collection("users").document(userId).update(updates).await()

                // Refresh user data
                loadUserProfile()
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
