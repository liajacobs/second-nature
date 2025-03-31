package com.example.secondnature.data.repository

import android.net.Uri
import android.util.Log
import com.example.secondnature.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class PostRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImageToStorage(imageUri: Uri): Result<String> {
        return try {
            val fileName = "post_images/${System.currentTimeMillis()}_${imageUri.lastPathSegment}"
            val storageRef = storage.reference.child(fileName)

            // Upload the image file
            storageRef.putFile(imageUri).await()

            // Get the image URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error uploading image: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getPost(postID: String): Result<Post> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val document = firestore.collection("posts").document(postID).get().await()
            document.toObject(Post::class.java)?.let { post ->
                val postWithId = post.copy(postId = document.id)
                Result.success(postWithId)
            } ?: Result.failure(Exception("Post not found"))
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching post: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllPosts(): Result<List<Post>> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val querySnapshot = firestore.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)?.copy(postId = document.id)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching all posts: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserPosts(): Result<List<Post>> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val posts = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Post::class.java)?.copy(postId = document.id)
            }
            Result.success(posts)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching user posts: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createPost(post: Post,  imageUri: Uri?): Result<String> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            // Upload image if provided
            val imageUrl = if (imageUri != null) {
                val uploadResult = uploadImageToStorage(imageUri)
                if (uploadResult.isFailure) return Result.failure(uploadResult.exceptionOrNull()!!)
                uploadResult.getOrThrow()
            } else {
                "" // No image provided
            }
            val postWithImage = post.copy(imageURL = imageUrl)
            val documentRef = firestore.collection("posts").add(post).await()
            val createdPost = post.copy(postId = documentRef.id)
            Result.success(createdPost.postId)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error creating post: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updatePost(post: Post): Result<Post> {
        Log.d("PostRepository", "Updating post with ID: ${post.postId}")
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val documentRef = firestore.collection("posts").document(post.postId)
            val updatedPost = post.copy(postId = "")
            documentRef.set(updatedPost, SetOptions.merge()).await()
            Result.success(post)
        } catch (e: Exception) {
            Log.e("PostRepository", "Error updating post: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<String> {
        return try {
            auth.currentUser?.uid ?: throw Exception("User not authenticated")
            firestore.collection("posts").document(postId).delete().await()
            Result.success("Post deleted")
        } catch (e: Exception) {
            Log.e("PostRepository", "Error deleting post: ${e.message}")
            Result.failure(e)
        }
    }

}