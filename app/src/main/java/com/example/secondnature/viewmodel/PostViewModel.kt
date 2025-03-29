package com.example.secondnature.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.secondnature.data.model.Post
import com.example.secondnature.data.repository.PostRepository
import com.example.secondnature.data.repository.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val postRepository = PostRepository()
    private val userRepository = UserRepository()

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?>
        get() = _post

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getPost(postID: String) {
        viewModelScope.launch {
            postRepository.getPost(postID).onSuccess { _post.value = it }.onFailure {
                _error.value = it.message ?: "Unknown error"
                _post.value = null
            }
        }
    }
    
    fun updatePost(post: Post, onPostEdited: (String) -> Unit) {
        viewModelScope.launch {
            try {
                userRepository
                        .getUserProfile()
                        .onSuccess { user ->
                            val updatedPost = post.copy(username = user.username)
                            Log.d("PostViewModel", "Updating post: $updatedPost")

                            postRepository
                                    .updatePost(updatedPost)
                                    .onSuccess {
                                        _post.value = it
                                        onPostEdited(it.postId)
                                    }
                                    .onFailure {
                                        _error.value = it.message ?: "Unknown error"
                                        _post.value = null
                                    }
                        }
                        .onFailure { _error.value = "Failed to get user profile: ${it.message}" }
            } catch (e: Exception) {
                _error.value = "Error updating post: ${e.message}"
            }
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            val result = postRepository.deletePost(postId)

            result
                    .onSuccess {
                        _post.value = null
                        Log.d("PostViewModel", "Post successfully deleted.")
                    }
                    .onFailure { exception ->
                        Log.e("PostViewModel", "Error deleting post: ${exception.message}")
                    }
        }
    }
}
