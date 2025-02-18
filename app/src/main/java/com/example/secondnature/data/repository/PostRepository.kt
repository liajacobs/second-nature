package com.example.secondnature.data.repository

import com.example.secondnature.data.model.Post
import java.time.LocalDateTime
import java.time.Month

class PostRepository {
    fun getPosts(): List<Post> {
        // Hardcoded posts, will get actual content from Firebase
        return listOf(
            Post(
                "https://media-photos.depop.com/b1/40794629/2057413339_d5586b8434374d6b8ce33fd222b10b56/P0.jpg",
                4,
                2,
                "Flower Child",
                "lia",
                LocalDateTime.of(2025, Month.JANUARY, 14, 4, 23),
                1.1
            ),
            Post(
                "https://media-photos.depop.com/b1/12248399/2478486451_b4156a1f93094e84a917838f39673c82/P0.jpg",
                5,
                1,
                "Goodwill",
                "brian",
                LocalDateTime.of(2024, Month.DECEMBER, 29, 8, 19),
                1.4
            ),
            Post(
                "https://media-photos.depop.com/b1/52142754/2478798962_34a83e3460534317ba7876af582245e6/P0.jpg",
                3,
                3,
                "Out of the Closet",
                "charlie",
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 18, 48),
                2.4
            )
        )
    }
}