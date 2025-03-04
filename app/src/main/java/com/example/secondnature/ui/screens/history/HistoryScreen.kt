package com.example.secondnature.ui.screens.history

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HistoryScreen() {
    Log.d("Lifecycle", "Entering HistoryScreen Composable")
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("History Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen()
}
