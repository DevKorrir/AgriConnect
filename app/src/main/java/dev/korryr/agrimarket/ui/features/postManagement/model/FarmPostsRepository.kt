
package dev.korryr.agrimarket.ui.features.postManagement.model

import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.flow.Flow

interface FarmPostsService {
    
    /**
     * Get all posts for a specific farmer
     */
    fun getAllPostsForFarmer(farmId: String): Flow<List<FarmPost>>
    
    /**
     * Update an existing farm post
     */
    suspend fun updatePost(post: FarmPost): Result<Unit>
    
    /**
     * Delete a farm post
     */
    suspend fun deletePost(postId: String): Result<Unit>
    
    /**
     * Upload a new image and return the URL
     */
    suspend fun uploadImage(imageUri: String): Result<String>
    
    /**
     * Delete an image from storage
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit>
}