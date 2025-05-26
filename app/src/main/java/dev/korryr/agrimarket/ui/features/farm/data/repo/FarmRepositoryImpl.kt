package dev.korryr.agrimarket.ui.features.farm.data.repo

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FarmRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
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

    override suspend fun uploadImageToStorage(uri: Uri, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

                // Compress image first
                val compressedUri = compressImage(uri, context)
                val finalUri = compressedUri ?: uri

                // Create unique file reference
                val timestamp = System.currentTimeMillis()
                val imageRef = storage.reference
                    .child("farm_profiles/$userId/profile_$timestamp.jpg")

                // Upload and get download URL
                imageRef.putFile(finalUri).await()
                imageRef.downloadUrl.await().toString()

            } catch (e: Exception) {
                throw Exception("Image upload failed: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun compressImage(uri: Uri, context: Context): Uri? {
        return withContext(Dispatchers.IO) {
            try {
               val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                   val source = ImageDecoder.createSource(context.contentResolver, uri)
                   ImageDecoder.decodeBitmap(source)
               } else {
                   @Suppress("DEPRECATION")
                   MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
               }

                // Resize if too large (max 1024px)
                val maxSize = 1024
                val ratio = minOf(
                    maxSize.toFloat() / bitmap.width,
                    maxSize.toFloat() / bitmap.height
                )

                val resizedBitmap = if (ratio < 1) {
                    Bitmap.createScaledBitmap(
                        bitmap,
                        (bitmap.width * ratio).toInt(),
                        (bitmap.height * ratio).toInt(),
                        true
                    )
                } else bitmap

                // Save compressed image to cache
                val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
                file.outputStream().use { out ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }

                Uri.fromFile(file)
            } catch (e: Exception) {
                null // Return null if compression fails, will use original
            }
        }
    }



}