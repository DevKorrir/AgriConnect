package dev.korryr.agrimarket.ui.features.market.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.korryr.agrimarket.ui.features.market.dataModel.repo.MarketRepository
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
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

    init {
        fetchAllPostsFromFirebase()
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
}
