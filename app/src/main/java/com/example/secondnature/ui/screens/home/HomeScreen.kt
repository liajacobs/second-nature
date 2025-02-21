package com.example.secondnature.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.viewmodel.PostViewModel

@Composable
fun HomeScreen(postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering HomeScreen Composable")
    val posts = postViewModel.posts.observeAsState(initial = emptyList())

    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(posts.value) { post ->
                Text("${post.number}. ${post.content}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}