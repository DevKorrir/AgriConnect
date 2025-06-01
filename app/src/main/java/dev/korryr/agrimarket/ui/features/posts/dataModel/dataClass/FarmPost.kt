package dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass

import com.google.firebase.Timestamp

data class FarmPost(
    val postId: String = "",
    val farmId: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val type : String = "", //eg livestock. crops
    val price: Double = 0.0,
    val quantity: Int = 0,
    val unit: String = "kg", // kg , litres
    val size: String = "1 acre",
    //val timestamp: Timestamp = Timestamp.now()
    val timestamp:  Long = 0L,
)
