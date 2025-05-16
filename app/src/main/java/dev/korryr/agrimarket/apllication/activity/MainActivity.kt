package dev.korryr.agrimarket.apllication.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.korryr.agrimarket.apllication.mainApp.AgriMarketingApp
import dev.korryr.agrimarket.ui.theme.AgriMarketTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            AgriMarketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AgriMarketingApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
