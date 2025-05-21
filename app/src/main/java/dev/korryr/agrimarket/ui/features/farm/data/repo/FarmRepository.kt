package dev.korryr.agrimarket.ui.features.farm.data.repo

import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile

interface FarmRepository {
    suspend fun getFarmByOwner(ownerUid: String): FarmProfile?
    suspend fun saveFarm(profile: FarmProfile)
}
