package com.example.secondnature.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.CreateAccountViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateAccountScreen(navController: NavController) {
    // State variables for user input
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // ViewModel for creating accounts
    val viewModel: CreateAccountViewModel = viewModel()

    // Coroutine scope for launching background tasks
    val scope = rememberCoroutineScope()

    // UI Layout
    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
    ) {
        // First Name TextField
        UserInputField(value = firstName, label = "First Name") { firstName = it }

        // Last Name TextField
        UserInputField(value = lastName, label = "Last Name") { lastName = it }

        // Email TextField
        UserInputField(value = email, label = "Email") { email = it }

        // Password TextField
        UserInputField(value = password, label = "Password", isPassword = true) { password = it }

        // Confirm Password TextField
        UserInputField(value = confirmPassword, label = "Confirm Password", isPassword = true) {
            confirmPassword = it
        }

        // Display error message if necessary
        if (errorMessage.isNotEmpty()) {
            Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Create Account Button
        CreateAccountButton(isLoading = isLoading) {
            if (password == confirmPassword) {
                isLoading = true
                scope.launch {
                    val success = viewModel.createAccount(firstName, lastName, email, password)
                    isLoading = false
                    if (success) {
                        // Navigate to login after successful sign-up
                        navController
                                .popBackStack() // Remove the current screen from the back stack
                        navController.navigate("login") // Navigate to login screen
                    } else {
                        errorMessage = "Account creation failed. Try again."
                    }
                }
            } else {
                errorMessage = "Passwords do not match."
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation to Login
        TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
        ) { Text("Already have an account? Login") }
    }
}

@Composable
fun UserInputField(
        value: String,
        label: String,
        isPassword: Boolean = false,
        onValueChange: (String) -> Unit
) {
    TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation =
                    if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CreateAccountButton(isLoading: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text("Create Account")
        }
    }
}
