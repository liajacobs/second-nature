package com.example.secondnature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import ui.screens.ui.LoginScreen
import ui.screens.ui.CreateAccountScreen
import ui.screens.ui.HomePage
import ui.screens.ui.ui.theme.SecondNatureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecondNatureTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        // Pass navController and onLoginSuccess as a lambda function
                        LoginScreen(navController = navController) {
                            // Navigate to the home screen on successful login
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    composable("createAccount") {
                        CreateAccountScreen(navController = navController) // Pass navController correctly
                    }
                    composable("home") {
                        HomePage(navController = navController)
                    }
                }
            }
        }
    }
}

