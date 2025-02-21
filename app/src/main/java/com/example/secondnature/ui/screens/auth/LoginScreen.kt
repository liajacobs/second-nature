package com.example.secondnature.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
        navController: NavController,
        viewModel: LoginViewModel = viewModel(),
        onLoginSuccess: () -> Unit
) {
    Log.d("Lifecycle", "Entering LoginScreen Composable")

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Create a coroutine scope for this composable
    val coroutineScope = rememberCoroutineScope()

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
    ) {
        // Email TextField
        TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button with loading state
        Button(
                onClick = {
                    isLoading = true
                    errorMessage = "" // Reset error message

                    // Launching a coroutine on the button click
                    coroutineScope.launch {
                        val loginSuccessful = viewModel.login(email, password)

                        if (loginSuccessful) {
                            onLoginSuccess()
                        } else {
                            isLoading = false
                            errorMessage = "Login failed. Please check your credentials."
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Disable button while loading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to Create Account screen
        TextButton(
                onClick = { navController.navigate("createAccount") },
                modifier = Modifier.fillMaxWidth()
        ) { Text("Create Account") }
    }
}
