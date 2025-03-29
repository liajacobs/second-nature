package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.data.model.Post
import com.example.secondnature.viewmodel.PostViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(navController: NavController, postId: String, postViewModel: PostViewModel = viewModel()) {
    Log.d("Lifecycle", "Entering EditPostScreen Composable")

    val (storeName, setStoreName) = remember { mutableStateOf("") }
    val (imageURL, setImageURL) = remember { mutableStateOf("") }
    val (storeRating, setStoreRating) = remember { mutableIntStateOf(0) }
    val (priceRating, setPriceRating) = remember { mutableIntStateOf(1) }
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(postId) {
        postId?.let {
            postViewModel.getPost(it)
        }
    }

    val post = postViewModel.post.observeAsState()

    LaunchedEffect(post.value) {
        post.value?.let {
            setStoreName(it.storeName)
            setImageURL(it.imageURL)
            setStoreRating(it.storeRating)
            setPriceRating(it.priceRating)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Post") },
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
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
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

            Text("Store Rating: $storeRating")
            Slider(
                value = storeRating.toFloat(),
                onValueChange = { setStoreRating(it.toInt()) },
                valueRange = 0f..5f,
                steps = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Price Rating: $priceRating")
            Slider(
                value = priceRating.toFloat(),
                onValueChange = { setPriceRating(it.toInt()) },
                valueRange = 1f..3f,
                steps = 1
            )

            Button(
                onClick = {
                    try {
                        val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                        post.value?.let { post ->
                            postViewModel.updatePost(
                                Post(
                                    postId = post.postId,
                                    imageURL = imageURL,
                                    storeRating = storeRating,
                                    priceRating = priceRating,
                                    storeName = storeName,
                                    username = post.username,
                                    date = Timestamp.now(),
                                    storeId = post.storeId,
                                    userId = userId
                                ),
                                onPostEdited = { postId ->
                                    navController.navigate("viewPost/$postId")
                                }
                            )
                        }
                    } catch (e: Exception) {
                        Log.e("EditPostScreen", "Error updating post: ${e.message}")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Post")
            }
        }
    }
}