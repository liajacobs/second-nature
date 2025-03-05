package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.viewmodel.PostViewModel

@Composable
fun ViewPostScreen(navController: NavController, postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering ViewPostScreen Composable")

    var postId = navController.currentBackStackEntry?.arguments?.getString("postId")

    LaunchedEffect(postId) {
        postId?.let {
            postViewModel.getPost(it) // Fetch post by postId
        }
    }

    val post = postViewModel.post.observeAsState()

    Column(modifier = Modifier.padding(16.dp)) {
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
        Button(
            onClick = {
                navController.navigate("editPost/${postId}") // Navigate to EditPostScreen
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Post")
        }
    }
}