package com.example.secondnature

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.secondnature.data.repository.AuthRepository
import com.example.secondnature.ui.components.Navbar
import com.example.secondnature.ui.navigation.NavigationItem
import com.example.secondnature.ui.screens.auth.CreateAccountScreen
import com.example.secondnature.ui.screens.auth.LoginScreen
import com.example.secondnature.ui.screens.history.HistoryScreen
import com.example.secondnature.ui.screens.home.HomeScreen
import com.example.secondnature.ui.screens.post.CreatePostScreen
import com.example.secondnature.ui.screens.post.EditPostScreen
import com.example.secondnature.ui.screens.post.ViewPostScreen
import com.example.secondnature.ui.screens.profile.ProfileScreen
import com.example.secondnature.ui.screens.search.SearchScreen
import com.example.secondnature.ui.screens.settings.SettingsScreen
import com.example.secondnature.ui.theme.SecondNatureTheme
import com.example.secondnature.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    private val authRepository = AuthRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Lifecycle", "MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            SecondNatureTheme {
                val navController = rememberNavController()
                val postViewModel: PostViewModel = viewModel()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(navController = navController, authRepository = authRepository) {
                            Log.d("Navigation", "Navigating to MainScreen")
                            navController.navigate("mainScreen") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("createAccount") {
                        CreateAccountScreen(navController = navController, authRepository = authRepository)
                    }
                    composable("mainScreen") { MainScreen(navController = navController) }
                    composable("settings") {
                        SettingsScreen(navController = navController)
                    }
                    composable("viewPost/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")
                        if (postId != null) {
                            ViewPostScreen(navController = navController, postViewModel = postViewModel)
                        }
                    }
                    composable("editPost/{postId}") { backStackEntry ->
                        val postId = backStackEntry.arguments?.getString("postId")
                        if (postId != null) {
                            EditPostScreen(navController = navController, postViewModel = postViewModel)
                        }
                    }
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
fun MainScreen(navController: NavController) {
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
            composable(NavigationItem.Post.route) { CreatePostScreen(navController = navController) }
            composable(NavigationItem.History.route) { HistoryScreen() }
            composable(NavigationItem.Profile.route) {
                ProfileScreen(navController = navController)
            }
        }
    }
}
