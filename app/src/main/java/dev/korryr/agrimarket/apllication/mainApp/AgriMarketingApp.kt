package dev.korryr.agrimarket.apllication.mainApp

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dev.korryr.agrimarket.ui.navigation.NavGraph

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AgriMarketingApp(
    modifier: Modifier = Modifier
){
    val navController = rememberNavController()
    Scaffold {
        NavGraph(navController = navController)
    }

}