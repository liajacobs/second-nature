package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.ui.navigation.NavigationItem
import com.example.secondnature.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostConfirmationScreen(
    navController: NavController,
    postViewModel: PostViewModel = viewModel(),
    postId: String
) {
    Log.d("Lifecycle", "Entering PostConfirmationScreen Composable")
    
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        postViewModel.getPost(postId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Created") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { navController.navigate(NavigationItem.Post.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Another Post")
            }

            Button(
                onClick = {
                    navController.navigate("editPost/${postId}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Post")
            }

            Button(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Post")
            }

            val post = postViewModel.post.observeAsState()
            post.value?.let { postData ->
                PostItem(
                    imageURL = postData.imageURL,
                    storeRating = postData.storeRating,
                    priceRating = postData.priceRating,
                    storeName = postData.storeName,
                    username = postData.username,
                    date = postData.date
                )
            } ?: run {
                Text("Loading...")
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