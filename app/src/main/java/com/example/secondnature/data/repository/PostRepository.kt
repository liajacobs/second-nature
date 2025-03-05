package com.example.secondnature.data.repository

import android.util.Log
import com.example.secondnature.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

class PostRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun getPost(postID: String): Result<Post> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val document = firestore.collection("posts").document(postID).get().await()
            document.toObject(Post::class.java)?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Post not found"))
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching post: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createPost(post: Post): Result<String> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val documentRef = firestore.collection("posts").add(post).await()
            Result.success(documentRef.id)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error creating post: ${e.message}")
            Result.failure(e)
        }
    }
}