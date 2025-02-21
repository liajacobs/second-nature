package com.example.secondnature.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.viewmodel.PostViewModel
import com.example.secondnature.ui.components.PostItem

@Composable
fun HomeScreen(postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering HomeScreen Composable")
    val posts = postViewModel.posts.observeAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(posts.value) { post ->
            PostItem(
                post.imageURL,
                post.storeRating,
                post.priceRating,
                post.storeName,
                post.username,
                post.date,
                post.distance
            )
        }
    }
}


@Preview
@Composable
fun Preview() {
    HomeScreen()
}