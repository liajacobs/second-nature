package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.data.repository.UserRepository
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.ui.navigation.NavigationItem
import com.example.secondnature.viewmodel.PostViewModel

@Composable
fun PostScreen(
    navController: NavController,
    postViewModel: PostViewModel = viewModel(),
    postId: String
) {
    Log.d("Lifecycle", "Entering PostScreen Composable")
    val userRepository = UserRepository()
    val currentUserId = userRepository.getCurrentUserId()
    
    LaunchedEffect(postId) {
        postViewModel.getPost(postId)
    }

    val post by postViewModel.post.observeAsState()
    val error by postViewModel.error.observeAsState()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                error != null -> {
                    Text(
                        text = error ?: "Unknown error occurred",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                post == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    // Display the post
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        post?.let { postData ->
                            PostItem(
                                imageURL = postData.imageURL,
                                storeRating = postData.storeRating,
                                priceRating = postData.priceRating,
                                storeName = postData.storeName,
                                username = postData.username,
                                date = postData.date
                            )
                            
                            // Only show edit and delete buttons if the post was made by the current user
                            if (currentUserId != null && postData.userId == currentUserId) {
                                // Edit button
                                FloatingActionButton(
                                    onClick = { navController.navigate("editPost/${postId}") },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 80.dp, bottom = 16.dp),
                                    containerColor = Color(0xFF03A9F4)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Post",
                                        tint = Color.White
                                    )
                                }
                                
                                // Delete button
                                FloatingActionButton(
                                    onClick = {
                                        postViewModel.deletePost(postId)
                                        navController.navigate(NavigationItem.Home.route)
                                    },
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 16.dp, bottom = 16.dp),
                                    containerColor = Color(0xFFF44336)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Post",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 