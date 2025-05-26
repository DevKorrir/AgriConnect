package dev.korryr.agrimarket.ui.features.farm.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.graphics.scale

@Singleton
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
                Timber.e("FarmRepository", "Uploading image for user: $userId")

                // Compress image first
                val compressedUri = compressImage(uri, context)
                val finalUri = compressedUri ?: uri

                // Create unique file reference
                val timestamp = System.currentTimeMillis()
                val imageRef = storage.reference
                    .child("farm_profiles/$userId/profile_$timestamp.jpg")

                Timber.tag("FarmRepository")
                    .d("Upload path: farm_profiles/$userId/profile_$timestamp.jpg")

                // Upload and get download URL
                imageRef.putFile(finalUri).await()
                imageRef.downloadUrl.await().toString()

            } catch (e: Exception) {
                Timber.tag("FarmRepository").e(e, "Upload failed")
                throw Exception("Image upload failed: ${e.localizedMessage}")
            }
        }
    }

//    private suspend fun compressImage(uri: Uri, context: Context): Uri? {
//        return withContext(Dispatchers.IO) {
//            try {
//               val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                   val source = ImageDecoder.createSource(context.contentResolver, uri)
//                   ImageDecoder.decodeBitmap(source)
//               } else {
//                   @Suppress("DEPRECATION")
//                   MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
//               }
//
//                // Resize if too large (max 1024px)
//                val maxSize = 1024
//                val ratio = minOf(
//                    maxSize.toFloat() / bitmap.width,
//                    maxSize.toFloat() / bitmap.height
//                )
//
//                val resizedBitmap = if (ratio < 1) {
//                    bitmap.scale((bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt())
//                } else bitmap
//
//                // Save compressed image to cache
//                val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
//                file.outputStream().use { out ->
//                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
//                }
//
//                Uri.fromFile(file)
//            } catch (e: Exception) {
//                null // Return null if compression fails, will use original
//            }
//        }
//    }

    private suspend fun compressImage(uri: Uri, context: Context): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                // Get bitmap with proper exception handling
                val bitmap = getBitmapFromUri(uri, context) ?: return@withContext null

                // Check if bitmap is valid
                if (bitmap.isRecycled || bitmap.width <= 0 || bitmap.height <= 0) {
                    return@withContext null
                }

                // Calculate resize ratio (max 1024px on longest side)
                val maxSize = 1024
                val ratio = minOf(
                    maxSize.toFloat() / bitmap.width,
                    maxSize.toFloat() / bitmap.height,
                    1.0f // Don't upscale
                )

                val finalBitmap = if (ratio < 1.0f) {
                    val newWidth = (bitmap.width * ratio).toInt()
                    val newHeight = (bitmap.height * ratio).toInt()

                    // Use FILTER_BILINEAR for better quality and stability
                    bitmap.scale(newWidth, newHeight)
                } else {
                    bitmap
                }

                // Save to cache directory with error handling
                val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")

                file.outputStream().use { out ->
                    val compressed = finalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    if (!compressed) {
                        return@withContext null
                    }
                }

                // Clean up bitmap if it's different from original
                if (finalBitmap != bitmap) {
                    finalBitmap.recycle()
                }
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }

                Uri.fromFile(file)

            } catch (e: Exception) {
                Timber.tag("FarmRepository").e(e, "Image compression failed")
                null // Return null to use original image
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    // Configure decoder for stability
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.setTargetSampleSize(1) // Don't auto-downsample
                }
            } else {
                // For older Android versions
                @Suppress("DEPRECATION")
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                    inSampleSize = 1
                }
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
            }
        } catch (e: Exception) {
            Timber.tag("FarmRepository").e(e, "Failed to decode bitmap")
            null
        }
    }


}