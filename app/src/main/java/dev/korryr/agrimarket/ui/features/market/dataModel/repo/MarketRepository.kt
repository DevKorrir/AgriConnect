package dev.korryr.agrimarket.ui.features.market.dataModel.repo

import com.google.firebase.firestore.FirebaseFirestore
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    /**
     * Reads all documents from Firestore “farm_posts” collection,
     * maps each document to FarmPost data class.
     */
    suspend fun getAllFarmPosts(): List<FarmPost> {
        return try {
            firestore.collection("farm_posts")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    // Each doc should have fields: imageUrl, description, price, quantity, unit, type, farmerId, etc.
                    doc.toObject(FarmPost::class.java)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
