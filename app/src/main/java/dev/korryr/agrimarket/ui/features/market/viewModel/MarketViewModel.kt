package dev.korryr.agrimarket.ui.features.market.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.market.dataModel.repo.MarketRepository
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    // 1) All farm posts as before
    private val _allPosts = MutableStateFlow<List<FarmPost>>(emptyList())
    val allPosts: StateFlow<List<FarmPost>> = _allPosts

    // 2) Loading indicator
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3) Distinct category list, derived from _allPosts
    private val _allTypes = MutableStateFlow<List<String>>(emptyList())
    val allTypes: StateFlow<List<String>> = _allTypes

    // if you want to show farm profiles in amap
    private val _allFarmProfiles = MutableStateFlow<Map<String, FarmProfile>>(emptyMap())
    val allFarmProfiles: StateFlow<Map<String, FarmProfile>> = _allFarmProfiles

//    init {
//        fetchAllPostsFromFirebase()
//    }

    init {
        // 1. Collect all posts
        viewModelScope.launch {
            repository.streamAllPosts()
                .onStart { _isLoading.value = true }
                .catch {
                    // You can log the error
                    _isLoading.value = false
                }
                .collect { posts ->
                    _allPosts.value = posts
                    _isLoading.value = false

                    // Build distinct, sorted categories:
                    val types = posts
                        .map { it.type.trim() }
                        .filter { it.isNotBlank() }
                        .toSet()
                        .sorted()
                    _allTypes.value = types
                }
        }

        // 2. Collect all farm profiles (so you can show farmName for each post’s farmId)
        viewModelScope.launch {
            // If you want to fetch all farm profiles in one go, there is no built-in "streamAll" example above,
            // but you could write a repository function streamAllFarmProfiles() that listens to `collection("farms")`.
            repository.streamAllFarmProfiles() // this returns Flow<List<FarmProfile>>
                .collect { profileList ->
                    // Convert to a Map<farmId, FarmProfile> for easier lookups
                    _allFarmProfiles.value = profileList.associateBy { it.farmId }
                }
        }
    }

    private fun fetchAllPostsFromFirebase() {
        viewModelScope.launch {
            _isLoading.value = true

            val posts = repository.getAllFarmPosts()
            _allPosts.value = posts

            // Compute distinct types from 'posts'
            // e.g. if posts = [ type="Livestock", type="Crops", type="Livestock" ]
            // distinct = ["Crops", "Livestock"]
            val typesSet = posts.map { it.type.trim() }
                                .filter { it.isNotBlank() }
                                .toSet()

            // Sort them alphabetically (or implement your own priority)
            val sortedList = typesSet.sorted()

            _allTypes.value = sortedList

            _isLoading.value = false
        }
    }

    // 3) Expose flows for “selected post details” (like count, user liked, comment count, bookmark)
    private val _selectedPostId = MutableStateFlow<String?>(null)
    val selectedPostId: StateFlow<String?> = _selectedPostId

    // 3a) Like count for the selected post
    val selectedLikeCount: StateFlow<Int> = _selectedPostId
        .filterNotNull()
        .flatMapLatest { postId -> repository.streamLikeCount(postId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 3b) Did current user like it?
    val selectedUserLiked: StateFlow<Boolean> = _selectedPostId
        .filterNotNull()
        .flatMapLatest { postId -> repository.streamUserLiked(postId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 3c) Comment count for the selected post
    val selectedCommentCount: StateFlow<Int> = _selectedPostId
        .filterNotNull()
        .flatMapLatest { postId -> repository.streamCommentCount(postId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // 3d) Bookmarked?
    val selectedBookmarked: StateFlow<Boolean> = _selectedPostId
        .filterNotNull()
        .flatMapLatest { postId -> repository.streamBookmarked(postId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 3e) If you ever need “Follower count” or “User follows this farm”
    private val _selectedFarmId = MutableStateFlow<String?>(null)
    val selectedFarmId: StateFlow<String?> = _selectedFarmId

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedFollowerCount: StateFlow<Int> = _selectedFarmId
        .filterNotNull()
        .flatMapLatest { farmId -> repository.streamFollowerCount(farmId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedUserFollows: StateFlow<Boolean> = _selectedFarmId
        .filterNotNull()
        .flatMapLatest { farmId -> repository.streamUserFollows(farmId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Helpers to call toggles
    fun onToggleLike(postId: String) = repository.toggleLike(postId)
    fun onToggleBookmark(postId: String) = repository.toggleBookMark(postId)

    // follow/ following
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    /**
     * Call this when you first display a post, to load the button state.
     */
    fun refreshFollowState(farmId: String) {
        viewModelScope.launch {
            _isFollowing.value = repository.isFollowing(farmId)
        }
    }

    /**
     * Called from your composable when the user taps Follow/Unfollow.
     */
    fun onToggleFollow(farmId: String) {
        viewModelScope.launch {
            // This suspend call will flip Firestore and return the new state.
            val newState = repository.toggleFollow(farmId)
            _isFollowing.value = newState
        }
    }

    // Call these when UI “selects” a post or farm to see details
    fun selectPost(postId: String) {
        _selectedPostId.value = postId
    }
    fun selectFarm(farmId: String) {
        _selectedFarmId.value = farmId
    }

}
