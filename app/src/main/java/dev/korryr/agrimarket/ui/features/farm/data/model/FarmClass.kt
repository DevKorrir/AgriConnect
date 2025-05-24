package dev.korryr.agrimarket.ui.features.farm.data.model

data class FarmProfile(
  val farmId: String = "",
  val ownerUid: String = "",
  val farmName: String = "",
  val location: String = "",
  val typeOfFarming: String = "",
  val contact: String = "",
  val createdAt: Long = System.currentTimeMillis()
)


