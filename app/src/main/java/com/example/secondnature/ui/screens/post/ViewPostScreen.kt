package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.viewmodel.PostViewModel

@Composable
fun ViewPostScreen(postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering PostScreen Composable")
    val postID = "ofGiKm3Tx0sEHqtI6qpI"

    LaunchedEffect(postID) {
        postViewModel.getPost(postID)
    }

    val post = postViewModel.post.observeAsState()

    post.value?.let {
        PostItem(
            imageURL = it.imageURL,
            storeRating = it.storeRating,
            priceRating = it.priceRating,
            storeName = it.storeName,
            username = it.username,
            date = it.date
        )
    } ?: run {
        Text("Post not found")
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    ViewPostScreen()
}
