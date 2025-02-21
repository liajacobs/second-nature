package com.example.secondnature.data.model

import java.time.LocalDateTime

// Define Post data class
data class Post(
    val imageURL: String,
    val storeRating: Int,
    val priceRating: Int,
    val storeName: String,
    val username: String,
    val date: LocalDateTime,
    val distance: Double
)