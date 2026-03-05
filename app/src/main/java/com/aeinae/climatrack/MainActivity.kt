package com.aeinae.climatrack

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aeinae.climatrack.navigation.BottomNavigationBar
import com.aeinae.climatrack.navigation.NavigationHost
import com.aeinae.climatrack.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            ClimaTrackTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {BottomNavigationBar(navController = navController, enabled = true)}
                    ) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    NavigationHost(modifier = modifier, navController = navController)
}

