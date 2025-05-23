package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavController,
    postViewModel: PostViewModel = viewModel(),
    postId: String
) {
    Log.d("Lifecycle", "Entering PostScreen Composable")
    val userRepository = UserRepository()
    val currentUserId = userRepository.getCurrentUserId()
    
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    
    LaunchedEffect(postId) {
        postViewModel.getPost(postId)
    }

    val post by postViewModel.post.observeAsState()
    val error by postViewModel.error.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Details") },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                                    onClick = { showDeleteConfirmation = true },
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
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        postViewModel.deletePost(postId)
                        showDeleteConfirmation = false
                        navController.navigate(NavigationItem.Home.route)
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
} 