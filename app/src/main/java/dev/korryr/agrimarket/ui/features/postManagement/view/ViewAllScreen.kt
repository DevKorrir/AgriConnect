package dev.korryr.agrimarket.ui.features.postManagement.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import dev.korryr.agrimarket.ui.features.postManagement.presentation.DeleteConfirmationDialog
import dev.korryr.agrimarket.ui.features.postManagement.presentation.EditPostDialog
import dev.korryr.agrimarket.ui.features.postManagement.presentation.EmptyPostsView
import dev.korryr.agrimarket.ui.features.postManagement.presentation.PostManagerCard
import dev.korryr.agrimarket.ui.features.postManagement.viewModel.ManagePostViewModel
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePostScreen(
    managePostViewModel: ManagePostViewModel = hiltViewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val posts by managePostViewModel.posts.collectAsState()
    val isLoading by managePostViewModel.isLoading.collectAsState()
    val error by managePostViewModel.error.collectAsState()
    val isUpdating by managePostViewModel.isUpdating.collectAsState()
    val updateSuccess by managePostViewModel.updateSuccess.collectAsState()
    val deleteSuccess by managePostViewModel.deleteSuccess.collectAsState()

    //dialog states
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedPost by remember { mutableStateOf<FarmPost?>(null) }

    // Load posts when screen appears
    LaunchedEffect(currentUid) {
        if (currentUid.isNotBlank()) {
            managePostViewModel.loadPosts(currentUid)
        }
    }

    // Handle success messages
    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            managePostViewModel.clearMessages()
        }
    }

    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            managePostViewModel.clearMessages()
        }
    }

    // Show error snackbar
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // You can show a snackbar here if needed
            managePostViewModel.clearMessages()
        }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Posts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { contentPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Loading your posts...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                posts.isEmpty() -> {
                    EmptyPostsView()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(posts) { post ->
                            PostManagerCard(
                                post = post,

                                onEditClick = {
                                    selectedPost = post
                                    showEditDialog = true
                                },
                                onDeleteClick = {
                                    selectedPost = post
                                    showDeleteDialog = true
                                }
                            )
                        }

                        // bottomBadding
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    //edit dialog
    if (showEditDialog && showDeleteDialog != null) {
        EditPostDialog(
            post = selectedPost!!,
            onDismiss = {
                showEditDialog = false
                selectedPost = null
            },
            onSave = { updatedPost ->
                managePostViewModel.updatePost(updatedPost)
                showEditDialog = false
                selectedPost = null
            },

        )
    }


    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedPost != null) {
        DeleteConfirmationDialog(
            postTitle = selectedPost!!.description,
            onConfirm = {
                managePostViewModel.deletePost(selectedPost!!.postId)
                showDeleteDialog = false
                selectedPost = null
            },
            onDismiss = {
                showDeleteDialog = false
                selectedPost = null
            }
        )
    }
}