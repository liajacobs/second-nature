package com.example.secondnature.ui.screens.post

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PostScreen() {
    Log.d("Lifecycle", "Entering PostScreen Composable")
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Post Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun PostScreenPreview() {
    PostScreen()
}
