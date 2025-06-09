package dev.korryr.agrimarket.ui.features.settings.view.apperances.view

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.korryr.agrimarket.ui.features.settings.view.apperances.presentation.ElegantThemeToggle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import dev.korryr.agrimarket.ui.theme.ThemeViewModel

@Composable
fun AppearanceScreen(
    themeManager: ThemeViewModel,
    modifier: Modifier = Modifier) {
    Box (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ){
        ElegantThemeToggle(
            isDarkMode = themeManager.isDarkThemeEnabled,
            onToggle = { themeManager.toggleTheme() },
            modifier = modifier
        )

    }

}