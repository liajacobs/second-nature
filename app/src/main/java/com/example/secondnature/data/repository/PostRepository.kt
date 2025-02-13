package com.example.secondnature.data.repository

import com.example.secondnature.data.model.Post

class PostRepository {
    fun getPosts(): List<Post> {
        // hardcoded posts, would replace with firebase call?
        return listOf(
            Post("First post", 1),
            Post("Second post", 2),
            Post("Third post", 3)
        )
    }
}