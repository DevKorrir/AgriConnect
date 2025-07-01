package dev.korryr.agrimarket.ui.features.postManagement.viewModel

import dev.korryr.agrimarket.ui.features.postManagement.model.FarmPostsService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagePostViewModel @Inject constructor(
    private val postsService: FarmPostsService
) : ViewModel() {

    private val _posts = MutableStateFlow<List<FarmPost>>(emptyList())
    val posts: StateFlow<List<FarmPost>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _deleteSuccess = MutableStateFlow(false)
    val deleteSuccess: StateFlow<Boolean> = _deleteSuccess.asStateFlow()

    fun loadPosts(farmerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                postsService.getAllPostsForFarmer(farmerId)
                    .collect { posts ->
                        _posts.value = posts
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load posts"
                _isLoading.value = false
            }

        }
    }

    fun updatePost(post: FarmPost) {
        viewModelScope.launch {
            _isUpdating.value = true
            _error.value = null
            _updateSuccess.value = false
            postsService.updatePost(post)
                .fold(
                    onSuccess = {
                        _isUpdating.value = false
                        _updateSuccess.value = true
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to update post"
                        _isUpdating.value = false
                    }
                )
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            _error.value = null
            _deleteSuccess.value = false

            postsService.deletePost(postId)
                .fold(
                    onSuccess = {
                        //remove deleted post from local state instantly
                        _posts.value = _posts.value.filterNot { it.postId == postId }
                        _isUpdating.value = false
                        _deleteSuccess.value = true
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Failed to delete post"
                        _isUpdating.value = false
                    }
                )
        }

    }

    fun uploadNewImage(imageUri: String, onResult: (Result<String>) -> Unit)  {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = postsService.uploadImage(imageUri)
            onResult(result)

            _isUpdating.value = false
        }
    }

    fun clearMessages() {
        _error.value = null
        _updateSuccess.value = false
        _deleteSuccess.value = false
    }





}