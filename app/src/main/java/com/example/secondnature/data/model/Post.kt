package com.example.secondnature.data.model

import com.google.firebase.Timestamp

data class Post(
    val imageURL: String = "",
    val storeRating: Int = 0,
    val priceRating: Int = 0,
    val storeName: String = "",
    val username: String = "",
    val date: Timestamp = Timestamp.now(),
    val storeId: String = "",
    val userId: String = ""
)