package com.example.secondnature.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun formatTimestamp(timestamp: Timestamp): String {
    val date = Date(timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000)
    val formatter = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    return formatter.format(date)
}

// Individual post
@Composable
fun PostItem(
    imageURL: String,
    storeRating: Int,
    priceRating: Int,
    storeName: String,
    username: String,
    date: Timestamp,
) {
    Log.d("Lifecycle", "Entering PostItem Composable")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(storeName, fontSize = 30.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("@$username says")
            StarRating(storeRating)
            PriceRating(priceRating)
        }
        AsyncImage(
            model = imageURL,
            contentDescription = null,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatTimestamp(date))
//            Text("0 mi")
        }
    }
}

// Filled in star rating
@Composable
fun StarRating(rating: Int) {
    Log.d("Lifecycle", "Entering StarRating Composable")
    Row {
        for (i in 1..5) {
            val fill = if (i <= rating) Color.Red else Color.Gray
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "$fill Star",
                tint = fill
            )
        }
    }
}

// Filled in price rating
@Composable
fun PriceRating(rating: Int) {
    Log.d("Lifecycle", "Entering PriceRating Composable")
    Row {
        for (i in 1..3) {
            val fill = if (i <= rating) Color.Red else Color.Gray
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = "$fill Dollar Sign",
                tint = fill
            )
        }
    }
}