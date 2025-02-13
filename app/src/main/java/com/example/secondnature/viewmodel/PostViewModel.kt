package com.example.secondnature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.secondnature.data.repository.PostRepository

class PostViewModel : ViewModel() {
    private val repository = PostRepository()

    val posts = liveData {
        val postList = repository.getPosts()
        emit(postList)
    }
}
