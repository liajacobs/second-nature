package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
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
        userId: String,
        onPostCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val post = Post(
                postId = "",
                imageURL = imageURL,
                storeRating = storeRating,
                priceRating = priceRating,
                storeName = storeName,
                username = username,
                date = date,
                storeId = storeId,
                userId = userId
            )

            postRepository.createPost(post).onSuccess {
                onPostCreated(it)
            }.onFailure {
                _error.value = it.message ?: "Unknown error"
                _post.value = null
            }
        }
    }

    fun updatePost(post: Post, onPostEdited: (String) -> Unit) {
        viewModelScope.launch {
            Log.d("PostViewModel", "Updating post: $post")
            postRepository.updatePost(post).onSuccess {
                _post.value = it
                onPostEdited(it.postId)
            }.onFailure {
                _error.value = it.message ?: "Unknown error"
                _post.value = null
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val result = postRepository.deletePost(postId)

            result.onSuccess {
                _post.value = null
                Log.d("PostViewModel", "Post successfully deleted.")
            }.onFailure { exception ->
                Log.e("PostViewModel", "Error deleting post: ${exception.message}")
            }
        }
    }
}