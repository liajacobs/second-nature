package com.example.secondnature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.model.User
import com.example.secondnature.data.repository.PostRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository()

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> get() = _post

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getPost(postID: String) {
        viewModelScope.launch {
            postRepository.getPost(postID).onSuccess {
                _post.value = it
            }.onFailure {
                _error.value = it.message ?: "Unknown error"
                _post.value = null
            }
        }
    }

    fun createPost(
        imageURL: String,
        storeRating: Int,
        priceRating: Int,
        storeName: String,
        username: String,
        date: Timestamp,
        storeId: String,
        userId: String
    ) {
        viewModelScope.launch {

        }
    }
}

