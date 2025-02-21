package com.example.secondnature

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.secondnature.ui.components.Navbar
import com.example.secondnature.ui.navigation.NavigationItem
import com.example.secondnature.ui.screens.auth.CreateAccountScreen
import com.example.secondnature.ui.screens.auth.LoginScreen
import com.example.secondnature.ui.screens.history.HistoryScreen
import com.example.secondnature.ui.screens.home.HomeScreen
import com.example.secondnature.ui.screens.post.PostScreen
import com.example.secondnature.ui.screens.profile.ProfileScreen
import com.example.secondnature.ui.screens.search.SearchScreen
import com.example.secondnature.ui.theme.SecondNatureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Lifecycle", "MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            SecondNatureTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController) {
                            Log.d("Navigation", "Navigating to MainScreen")
                            navController.navigate("mainScreen") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("createAccount") {
                        CreateAccountScreen(navController = navController)
                    }
                    composable("mainScreen") { MainScreen() }
                }
            }
        }
    }
    override fun onStart() {
        Log.d("Lifecycle", "MainActivity onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("Lifecycle", "MainActivity onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("Lifecycle", "MainActivity onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("Lifecycle", "MainActivity onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d("Lifecycle", "MainActivity onDestroy")
        super.onDestroy()
    }
}

@Composable
fun MainScreen() {
    val nestedNavController = rememberNavController()

    Scaffold(
            bottomBar = {
                Navbar(navController = nestedNavController) // Pass the nested controller
            }
    ) { innerPadding ->
        NavHost(
                navController = nestedNavController,
                startDestination = NavigationItem.Home.route,
                modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Home.route) { HomeScreen() }
            composable(NavigationItem.Search.route) { SearchScreen() }
            composable(NavigationItem.Post.route) { PostScreen() }
            composable(NavigationItem.History.route) { HistoryScreen() }
            composable(NavigationItem.Profile.route) { ProfileScreen() }
        }
    }
}
