package dev.korryr.agrimarket.ui.features.posts.dataModel.repo

import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost

interface FarmPostRepository {
    suspend fun createPost(post: FarmPost): String
    suspend fun getRecentPosts(farmId: String): List<FarmPost>
}
