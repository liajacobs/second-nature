package com.example.secondnature.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Store name - headline
            Text(
                text = storeName,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // User info + ratings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "@$username says",
                        fontSize = 16.sp,
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRating(storeRating)
                    Spacer(modifier = Modifier.width(12.dp))
                    PriceRating(priceRating)
                }
            }

            // Conditionally show image
            if (imageURL.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = imageURL,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = formatTimestamp(date),
                fontSize = 16.sp,
            )
        }
    }
}


@Composable
fun StarRating(rating: Int) {
    Log.d("Lifecycle", "Entering StarRating Composable")
    Row {
        for (i in 1..5) {
            val fill = if (i <= rating) Color(0xFFFF9800) else Color.LightGray
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "$fill Star",
                tint = fill,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PriceRating(rating: Int) {
    Log.d("Lifecycle", "Entering PriceRating Composable")
    Row {
        for (i in 1..3) {
            val fill = if (i <= rating) Color(0xFF4CAF50) else Color.LightGray
            Icon(
                imageVector = Icons.Default.AttachMoney,
                contentDescription = "$fill Dollar Sign",
                tint = fill,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
