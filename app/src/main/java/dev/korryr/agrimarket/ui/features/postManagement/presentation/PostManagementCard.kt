package dev.korryr.agrimarket.ui.features.postManagement.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.korryr.agrimarket.ui.features.posts.dataModel.dataClass.FarmPost
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
import javax.annotation.meta.When
import dev.korryr.agrimarket.ui.features.postManagement.presentation.PostManagerCard

@Composable
fun  PostManagerCard(
    post: FarmPost,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

}