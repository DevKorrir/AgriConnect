package dev.korryr.agrimarket.ui.features.farm.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FarmRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FarmRepository {
    private val collection = firestore.collection("farms")

    override suspend fun getFarmByOwner(ownerUid: String): FarmProfile? {
        val doc = collection.document(ownerUid).get().await()
        return if (doc.exists()) doc.toObject<FarmProfile>()?.copy(farmId = doc.id) else null
    }

    override suspend fun saveFarm(profile: FarmProfile) {
        // use ownerUid as document id
        val docRef = collection.document(profile.ownerUid)
        docRef.set(profile.copy(farmId = profile.ownerUid)).await()
    }
}