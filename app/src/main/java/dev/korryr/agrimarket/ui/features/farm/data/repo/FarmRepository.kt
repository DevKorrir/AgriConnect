package dev.korryr.agrimarket.ui.features.farm.data.repo

import android.content.Context
import android.net.Uri
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile

interface FarmRepository {
    suspend fun getFarmByOwner(ownerUid: String): FarmProfile?
    suspend fun saveFarm(profile: FarmProfile)
    suspend fun uploadImageToStorage(uri: Uri, context: Context): String
}
