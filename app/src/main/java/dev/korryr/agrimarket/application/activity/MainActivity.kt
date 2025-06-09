package dev.korryr.agrimarket.application.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.korryr.agrimarket.ui.navigation.NavGraph
import dev.korryr.agrimarket.ui.theme.AgriMarketTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val paddingValues = PaddingValues()

            AgriMarketTheme {
                //Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    AgriMarketingApp(
//                        modifier = Modifier.padding(1.dp)
//                    )
                NavGraph(
                    modifier = Modifier,
                    navController = navController,
                    scaffoldPadding = paddingValues
                )
               // }
            }
        }
    }
}
