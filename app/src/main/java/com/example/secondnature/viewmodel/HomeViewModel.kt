package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.repository.PostRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val postRepository = PostRepository()

    val posts: LiveData<List<Post>> = postRepository.allPosts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    init {
        fetchPosts(false)
    }

    fun fetchPosts(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                postRepository
                        .getAllPosts(forceRefresh)
                        .onSuccess { _error.value = null }
                        .onFailure { exception ->
                            _error.value = exception.message ?: "Failed to fetch posts"
                        }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching posts: ${e.message}")
                _error.value = e.message ?: "An unexpected error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
