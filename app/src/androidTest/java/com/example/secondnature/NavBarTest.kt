package com.example.secondnature

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.secondnature.ui.components.Navbar
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.navigation.compose.rememberNavController
import com.example.secondnature.ui.screens.home.HomeScreen
import com.example.secondnature.ui.screens.profile.ProfileScreen
import com.example.secondnature.viewmodel.HomeViewModel

@RunWith(AndroidJUnit4::class)
class NavbarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navbar_displaysAllNavigationItems() {

        composeTestRule.setContent {
            val navController = rememberNavController()
            Navbar(navController = navController, onNavigate = {})
        }

        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
        composeTestRule.onNodeWithText("Post").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Profile").assertIsDisplayed()
    }

    @Test
    fun navbar_clickOnNavigationItem_triggersNavigation() {
        var navigatedRoute: String? = null

        composeTestRule.setContent {
            Navbar(
                navController = rememberNavController(),
                onNavigate = { route -> navigatedRoute = route }
            )
        }

        composeTestRule.onNodeWithText("Search").performClick()
        assert(navigatedRoute == "search") // Assuming route is "search"
    }


}