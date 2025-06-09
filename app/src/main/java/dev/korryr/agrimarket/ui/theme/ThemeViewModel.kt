package dev.korryr.agrimarket.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    var isDarkThemeEnabled by mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkThemeEnabled = !isDarkThemeEnabled
    }

}