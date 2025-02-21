package com.example.secondnature.ui.screens.auth

import android.util.Log
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
    Log.d("Lifecycle", "Entering CreateAccountScreen Composable")

    // State variables for user input
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
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

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name TextField
        UserInputField(value = lastName, label = "Last Name") { lastName = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Email TextField
        UserInputField(value = email, label = "Email") { email = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Username TextField
        UserInputField(value = username, label = "Username") { username = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        UserInputField(
                value = password,
                label = "Password",
                isPassword = true,
                onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password TextField
        UserInputField(
                value = confirmPassword,
                label = "Confirm Password",
                isPassword = true,
                onValueChange = { confirmPassword = it }
        )

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
        Button(
                onClick = {
                    if (validateInputs(
                                    firstName,
                                    lastName,
                                    email,
                                    username,
                                    password,
                                    confirmPassword
                            )
                    ) {
                        isLoading = true
                        scope.launch {
                            val success =
                                    viewModel.createAccount(
                                            firstName,
                                            lastName,
                                            email,
                                            username,
                                            password
                                    )
                            isLoading = false
                            if (success) {
                                navController.popBackStack()
                                navController.navigate("login")
                            } else {
                                errorMessage = "Account creation failed. Please try again."
                            }
                        }
                    } else {
                        // TODO: Show specific error message for each field
                        errorMessage = "Please fill in all fields correctly."
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Create Account")
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
    Log.d("Lifecycle", "Entering UserInputField Composable")
    TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation =
                    if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
    )
}

private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
): Boolean {
    return firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            email.isNotBlank() &&
            username.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmPassword &&
            password.length >= 6 &&
            email.contains("@")
}
