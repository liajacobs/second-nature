package com.example.secondnature

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import com.example.secondnature.ui.screens.post.PostConfirmationScreen
import com.example.secondnature.ui.screens.post.CreatePostScreen
import com.example.secondnature.ui.screens.post.EditPostScreen
import com.example.secondnature.ui.screens.post.PostScreen
import com.example.secondnature.ui.screens.profile.ProfileScreen
import com.example.secondnature.ui.screens.search.SearchScreen
import com.example.secondnature.ui.screens.search.StoreScreen
import com.example.secondnature.ui.screens.settings.SettingsScreen
import com.example.secondnature.ui.theme.SecondNatureTheme
import com.example.secondnature.viewmodel.LocationViewModel
import com.example.secondnature.viewmodel.PostViewModel

class MainActivity : ComponentActivity() {
    private val authRepository = AuthRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Lifecycle", "MainActivity onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            val locationViewModel: LocationViewModel = viewModel()
            locationViewModel.getCurrentLocation()
            SecondNatureTheme {
                AppContent(authRepository)
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
fun AppContent(authRepository: AuthRepository) {
    val navController = rememberNavController()
    val postViewModel: PostViewModel = viewModel()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute?.let { route ->
        !route.startsWith("login") && !route.startsWith("createAccount")
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Navbar(
                    navController = navController,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    navController = navController,
                    authRepository = authRepository
                ) {
                    Log.d("Navigation", "Navigating to Home")
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            composable("createAccount") {
                CreateAccountScreen(navController = navController, authRepository = authRepository)
            }
            composable(NavigationItem.Home.route) { HomeScreen(navController = navController) }
            composable(NavigationItem.Search.route) { SearchScreen(navController = navController) }
            composable(NavigationItem.Post.route) { CreatePostScreen(navController = navController) }
            composable(NavigationItem.History.route) { HistoryScreen(navController = navController) }
            composable(NavigationItem.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }
            composable(
                route = "viewPost/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                if (postId != null) {
                    PostConfirmationScreen(
                        navController = navController,
                        postId = postId
                    )
                }
            }
            composable(
                route = "post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                if (postId != null) {
                    PostScreen(
                        navController = navController,
                        postId = postId
                    )
                }
            }
            composable(
                route = "editPost/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId")
                if (postId != null) {
                    EditPostScreen(
                        navController = navController,
                    )
                }
            }
            composable(
                route = "store/{placeId}",
                arguments = listOf(navArgument("placeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val placeId = backStackEntry.arguments?.getString("placeId")
                if (placeId != null) {
                    StoreScreen(
                        navController = navController,
                    )
                }
            }
        }
    }
}
