package com.example.secondnature.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering HomeScreen Composable")

    val posts by homeViewModel.posts.observeAsState(initial = emptyList())
    val isLoading by homeViewModel.isLoading.observeAsState(initial = true)
    val error by homeViewModel.error.observeAsState()

    val refreshState = rememberPullToRefreshState()
    if (refreshState.isRefreshing) {
        LaunchedEffect(true) {
            homeViewModel.fetchPosts()
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
                            Text(text = "No posts available")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(posts) { post ->
                                PostItem(
                                    imageURL = post.imageURL,
                                    storeRating = post.storeRating,
                                    priceRating = post.priceRating,
                                    storeName = post.storeName,
                                    username = post.username,
                                    date = post.date
                                )
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
}