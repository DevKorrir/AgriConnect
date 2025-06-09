package dev.korryr.agrimarket.ui.features.settings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThemeToggle(
    isDarkMode: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Theme:",
            fontSize = 12.sp,
            color = if (isDarkMode) Color(0xFFCBD5E0) else Color(0xFF718096)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Light mode option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { if (isDarkMode) onToggle() }
            ) {
                RadioButton(
                    selected = !isDarkMode,
                    onClick = { if (isDarkMode) onToggle() },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFFFFA726)
                    )
                )
                Icon(
                    imageVector = Icons.Default.LightMode,
                    contentDescription = "Light",
                    tint = Color(0xFFFFA726),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            // Dark mode option
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { if (!isDarkMode) onToggle() }
            ) {
                RadioButton(
                    selected = isDarkMode,
                    onClick = { if (!isDarkMode) onToggle() },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color(0xFF5C6BC0)
                    )
                )
                Icon(
                    imageVector = Icons.Default.DarkMode,
                    contentDescription = "Dark",
                    tint = Color(0xFF5C6BC0),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}