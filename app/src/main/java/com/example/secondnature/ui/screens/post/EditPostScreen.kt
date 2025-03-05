package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.example.secondnature.data.model.Post
import com.example.secondnature.ui.components.PostItem
import com.example.secondnature.viewmodel.PostViewModel
import com.google.firebase.Timestamp

@Composable
fun EditPostScreen(navController: NavController, postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering EditPostScreen Composable")

    val postId = navController.currentBackStackEntry?.arguments?.getString("postId")
    val (storeName, setStoreName) = remember { mutableStateOf("") }
    val (imageURL, setImageURL) = remember { mutableStateOf("") }

    LaunchedEffect(postId) {
        postId?.let {
            postViewModel.getPost(it) // Trigger to fetch post from viewModel
        }
    }

    val post = postViewModel.post.observeAsState()

    Log.d("PostViewModel", "post is $post and post id is $postId")

    LaunchedEffect(post.value) {
        post.value?.let {
            setStoreName(it.storeName)
            setImageURL(it.imageURL)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = storeName,
            onValueChange = setStoreName,
            label = { Text("Store Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = imageURL,
            onValueChange = setImageURL,
            label = { Text("Image URL") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                post.value?.let { post ->
                        postViewModel.updatePost(
                            Post(
                                postId = post.postId,
                                imageURL = imageURL,
                                storeRating = 1,
                                priceRating = 2,
                                storeName = storeName,
                                username = "TestUser",
                                date = Timestamp.now(),
                                storeId = "storeId",
                                userId = "userId",
                            )
                        )
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Post")
        }
    }
}