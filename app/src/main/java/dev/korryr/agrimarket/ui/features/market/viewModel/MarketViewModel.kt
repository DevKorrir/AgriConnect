package dev.korryr.agrimarket.ui.features.market.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.netObserver.ConnectivityObserver
import dev.korryr.agrimarket.netObserver.NetworkStatus
import dev.korryr.agrimarket.ui.features.farm.data.model.FarmProfile
import dev.korryr.agrimarket.ui.features.market.dataModel.repo.MarketRepository
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: MarketRepository,
    private val auth: com.google.firebase.auth.FirebaseAuth,
    private val connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    // Expose the network status as a state flow
    val networkStatus: StateFlow<NetworkStatus> =
        connectivityObserver.observe()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = NetworkStatus.Unavailable
            )


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
    private val userId = auth.currentUser?.uid ?: ""

    // SOLUTION 1: Use a Map to store per-post state consistently
    private val _postLikeStates = MutableStateFlow<Map<String, PostLikeState>>(emptyMap())
    val postLikeStates: StateFlow<Map<String, PostLikeState>> = _postLikeStates.asStateFlow()

    // Data class to hold post interaction state
    data class PostLikeState(
        val likeCount: Int = 0,
        val isLiked: Boolean = false,
        val commentCount: Int = 0,
        val isBookmarked: Boolean = false
    )

    private val _postsCount = MutableStateFlow(0)
    val postsCount: StateFlow<Int> = _postsCount.asStateFlow()

    private val _followersCount = MutableStateFlow(0)
    val followersCount: StateFlow<Int> = _followersCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
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

        viewModelScope.launch {
            // Load initial posts count however you do it:
            _postsCount.value = repository.getAllFarmPosts().size

            // Load follow counts
            _followersCount.value = repository.getFollowersCount(userId)
            _followingCount.value = repository.getFollowingCount(userId)
        }

    }

    // SOLUTION 1: Single method to get like state for any post
    fun getLikeStateForPost(postId: String): StateFlow<PostLikeState> {
        return _postLikeStates.map { states ->
            states[postId] ?: PostLikeState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PostLikeState()
        )
    }

    // Initialize observation for a specific post (call once per post)
    fun observePostInteractions(postId: String) {
        if (_postLikeStates.value.containsKey(postId)) {
            return // Already observing
        }

        viewModelScope.launch {
            // Combine all post interaction streams
            combine(
                repository.streamLikeCount(postId),
                repository.streamUserLiked(postId),
                repository.streamCommentCount(postId),
                repository.streamBookmarked(postId)
            ) { likeCount, isLiked, commentCount, isBookmarked ->
                PostLikeState(
                    likeCount = likeCount,
                    isLiked = isLiked,
                    commentCount = commentCount,
                    isBookmarked = isBookmarked
                )
            }.collect { newState ->
                _postLikeStates.value = _postLikeStates.value.toMutableMap().apply {
                    put(postId, newState)
                }
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

//    // 3a) Like count for the selected post
//    val selectedLikeCount: StateFlow<Int> = _selectedPostId
//        .filterNotNull()
//        .flatMapLatest { postId -> repository.streamLikeCount(postId) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    // 3b) Did current user like it?
//    val selectedUserLiked: StateFlow<Boolean> = _selectedPostId
//        .filterNotNull()
//        .flatMapLatest { postId -> repository.streamUserLiked(postId) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

//    @OptIn(ExperimentalCoroutinesApi::class)
//    val selectedFollowerCount: StateFlow<Int> = _selectedFarmId
//        .filterNotNull()
//        .flatMapLatest { farmId -> repository.streamFollowerCount(farmId) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    val selectedUserFollows: StateFlow<Boolean> = _selectedFarmId
//        .filterNotNull()
//        .flatMapLatest { farmId -> repository.streamUserFollows(farmId) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Exposed to Compose
//    private val _likeCount = MutableStateFlow(0)
//    val likeCount: StateFlow<Int> = _likeCount
//
//    private val _userLiked = MutableStateFlow(false)
//    val userLiked: StateFlow<Boolean> = _userLiked

    /** Call once per post on composition: wires up real‑time streams */
//    fun observeLikes(postId: String) {
//        viewModelScope.launch {
//            repository.streamLikeCount(postId)
//                .collect { _likeCount.value = it }
//        }
//        viewModelScope.launch {
//            repository.streamUserLiked(postId)
//                .collect { _userLiked.value = it }
//        }
//    }

    // Toggle like with optimistic updates
    fun onToggleLike(postId: String) {
        viewModelScope.launch {
            val currentState = _postLikeStates.value[postId] ?: PostLikeState()

            // Optimistic update
            val optimisticState = currentState.copy(
                isLiked = !currentState.isLiked,
                likeCount = currentState.likeCount + if (currentState.isLiked) -1 else 1
            )

            _postLikeStates.value = _postLikeStates.value.toMutableMap().apply {
                put(postId, optimisticState)
            }

            try {
                // Perform actual operation
                repository.toggleLike(postId)
                // The real-time listener will update the state automatically
            } catch (e: Exception) {
                // Revert optimistic update on error
                _postLikeStates.value = _postLikeStates.value.toMutableMap().apply {
                    put(postId, currentState)
                }
            }
        }
    }

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