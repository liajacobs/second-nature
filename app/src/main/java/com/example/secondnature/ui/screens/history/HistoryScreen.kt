package com.example.secondnature.ui.screens.history

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.viewmodel.HistoryViewModel
import com.example.secondnature.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel = viewModel(),
    postViewModel: PostViewModel = viewModel(),
    navController: NavController
) {
    Log.d("Lifecycle", "Entering HistoryScreen Composable")

    val posts by historyViewModel.posts.observeAsState(initial = emptyList())
    val isLoading by historyViewModel.isLoading.observeAsState(initial = true)
    val error by historyViewModel.error.observeAsState()
    val hasMorePosts by historyViewModel.hasMorePosts.observeAsState(initial = false)
    
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var postToDelete by remember { mutableStateOf<String?>(null) }

    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            historyViewModel.fetchUserPosts()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            error != null -> {
                Text(text = error ?: "Unknown error occurred")
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(refreshState.nestedScrollConnection)
                ) {
                    if (posts.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "You haven't made any posts yet")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(posts) { post ->
                                var showMenu by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("post/${post.postId}")
                                        }
                                ) {
                                    PostItem(
                                        imageURL = post.imageURL,
                                        storeRating = post.storeRating,
                                        priceRating = post.priceRating,
                                        storeName = post.storeName,
                                        username = post.username,
                                        date = post.date
                                    )
                                    
                                    Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                        IconButton(
                                            onClick = { showMenu = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = "More options"
                                            )
                                        }

                                        DropdownMenu(
                                            expanded = showMenu,
                                            onDismissRequest = { showMenu = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Edit Post") },
                                                onClick = {
                                                    showMenu = false
                                                    navController.navigate("editPost/${post.postId}")
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Delete Post") },
                                                onClick = {
                                                    showMenu = false
                                                    postToDelete = post.postId
                                                    showDeleteConfirmation = true
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            
                            if (hasMorePosts) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Button(
                                            onClick = { historyViewModel.loadMorePosts() }
                                        ) {
                                            Text("Load More")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (refreshState.progress > 0f) {
                        PullToRefreshContainer(
                            modifier = Modifier.align(Alignment.TopCenter),
                            state = refreshState,
                        )
                    }
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            refreshState.endRefresh()
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmation && postToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmation = false 
                postToDelete = null
            },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        postToDelete?.let { id ->
                            postViewModel.deletePost(id)
                            historyViewModel.fetchUserPosts()
                        }
                        showDeleteConfirmation = false
                        postToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { 
                        showDeleteConfirmation = false 
                        postToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
