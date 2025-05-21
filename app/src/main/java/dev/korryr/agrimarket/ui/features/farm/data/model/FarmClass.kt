package dev.korryr.agrimarket.ui.features.farm.data.model

data class FarmProfile(
  val farmId: String = "",
  val ownerUid: String = "",
  val farmName: String = "",
  val location: String = "",
  val description: String = "",
  val contactInfo: String = ""
)

//data class FarmProfileUiState(
//  val profile: FarmProfile? = null,
//  val isSaving: Boolean = false,
//  val errorMessage: String? = null
//)

