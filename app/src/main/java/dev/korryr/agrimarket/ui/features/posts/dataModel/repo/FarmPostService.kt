package dev.korryr.agrimarket.ui.features.posts.dataModel.repo

import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.flow.Flow

interface FarmPostService {
    suspend fun createPost(post: FarmPost): Result<String>
    suspend fun getRecentPosts(farmId: String): Flow<List<FarmPost>>
    suspend fun getAllPostsForFarmer(farmId: String): Flow<List<FarmPost>>
}
