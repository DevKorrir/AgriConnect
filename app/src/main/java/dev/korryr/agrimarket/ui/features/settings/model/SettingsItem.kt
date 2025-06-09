package dev.korryr.agrimarket.ui.features.settings.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SettingsItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val hasArrow: Boolean = true,
    val isTheme: Boolean = false
)