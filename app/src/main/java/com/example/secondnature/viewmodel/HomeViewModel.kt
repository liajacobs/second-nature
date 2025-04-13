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
    private val pageSize = 10
    
    private val _allPosts = MutableLiveData<List<Post>>(emptyList())
    private val _displayedPosts = MutableLiveData<List<Post>>(emptyList())
    val posts: LiveData<List<Post>> get() = _displayedPosts
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error
    
    private val _hasMorePosts = MutableLiveData<Boolean>()
    val hasMorePosts: LiveData<Boolean> get() = _hasMorePosts

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                postRepository.getAllPosts()
                    .onSuccess { posts ->
                        _allPosts.value = posts
                        _displayedPosts.value = posts.take(pageSize)
                        _hasMorePosts.value = posts.size > pageSize
                        _error.value = null
                    }
                    .onFailure { exception ->
                        _error.value = exception.message ?: "Failed to fetch posts"
                        _allPosts.value = emptyList()
                        _displayedPosts.value = emptyList()
                        _hasMorePosts.value = false
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching posts: ${e.message}")
                _error.value = e.message ?: "An unexpected error occurred"
                _allPosts.value = emptyList()
                _displayedPosts.value = emptyList()
                _hasMorePosts.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadMorePosts() {
        val allPosts = _allPosts.value ?: emptyList()
        val currentPosts = _displayedPosts.value ?: emptyList()
        
        if (currentPosts.size < allPosts.size) {
            val nextPageSize = currentPosts.size + pageSize
            _displayedPosts.value = allPosts.take(nextPageSize)
            _hasMorePosts.value = nextPageSize < allPosts.size
        } else {
            _hasMorePosts.value = false
        }
    }
} 