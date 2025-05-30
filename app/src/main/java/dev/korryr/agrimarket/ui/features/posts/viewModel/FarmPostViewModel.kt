package dev.korryr.agrimarket.ui.features.posts.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import dev.korryr.agrimarket.ui.features.posts.dataModel.repo.FarmPostRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FarmPostViewModel @Inject constructor(
    private val repository: FarmPostRepositoryImpl,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _recentPosts = MutableStateFlow<List<FarmPost>>(emptyList())
    val recentPosts: StateFlow<List<FarmPost>> = _recentPosts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createPost(
        imageUrl: String,
        description: String,
        price: Double,
        quantity: Int,
        size: String
    ){
        viewModelScope.launch {
            try{
                _isPosting.value = true
                val uid = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                val post = FarmPost(
                    farmId = uid,
                    imageUrl = imageUrl,
                    description = description,
                    price = price,
                    quantity = quantity,
                    size = size
                )
                repository.createPost(post)
                loadRecentPosts(uid)
                _isPosting.value = false
            } catch (e: Exception){
                _error.value = e.localizedMessage
                _isPosting.value = false

            }
        }
    }

    private fun loadRecentPosts(farmId: String) {
        viewModelScope.launch {
            try {
                _recentPosts.value = repository.getRecentPosts(farmId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }














}