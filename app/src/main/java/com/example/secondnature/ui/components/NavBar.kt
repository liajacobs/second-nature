package com.example.secondnature.ui.components

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.secondnature.ui.navigation.NavigationItem

@Composable
fun Navbar(
    navController: NavController,
    onNavigate: (String) -> Unit
) {
    Log.d("Lifecycle", "Entering Navbar Composable")
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Search,
        NavigationItem.Post,
        NavigationItem.History,
        NavigationItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { dest ->
                when {
                    dest.route == item.route -> true
                    item == NavigationItem.Post && (
                        dest.route?.startsWith("createPost") == true || 
                        dest.route?.startsWith("viewPost") == true || 
                        dest.route?.startsWith("editPost") == true) -> true
                    else -> false
                }
            } ?: false

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                selected = isSelected,
                onClick = {
                    val route = if (item == NavigationItem.Post) "createPost/null" else item.route
                    onNavigate(route)
                }
            )
        }
    }
}
