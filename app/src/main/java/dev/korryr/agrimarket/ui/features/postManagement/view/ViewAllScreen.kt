package dev.korryr.agrimarket.ui.features.postManagement.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.korryr.agrimarket.ui.features.postManagement.viewModel.ManagePostViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import dev.korryr.agrimarket.ui.features.postManagement.presentation.EmptyPostsView
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
import javax.annotation.meta.When
import dev.korryr.agrimarket.ui.features.postManagement.presentation.PostManagerCard

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
    ){ contentPadding ->

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
                                    //selectedPost = post
                                    //showEditDialog = true
                                },
                                onDeleteClick = {

                                },
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

}