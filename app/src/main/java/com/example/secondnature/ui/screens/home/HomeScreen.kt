package com.example.secondnature.ui.screens.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.secondnature.ui.screens.post.ViewPostScreen

@Composable
fun HomeScreen() {
    Log.d("Lifecycle", "Entering HomeScreen Composable")
    ViewPostScreen() // Change to show all posts
}


@Preview
@Composable
fun Preview() {
    HomeScreen()
}