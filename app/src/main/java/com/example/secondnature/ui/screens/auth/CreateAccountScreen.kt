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

data class ValidationErrors(
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val emailError: String? = null,
        val usernameError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null
)

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
    var validationErrors by remember { mutableStateOf(ValidationErrors()) }

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
        UserInputField(
                value = firstName,
                label = "First Name",
                errorMessage = validationErrors.firstNameError
        ) { firstName = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Last Name TextField
        UserInputField(
                value = lastName,
                label = "Last Name",
                errorMessage = validationErrors.lastNameError
        ) { lastName = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Email TextField
        UserInputField(value = email, label = "Email", errorMessage = validationErrors.emailError) {
            email = it
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Username TextField
        UserInputField(
                value = username,
                label = "Username",
                errorMessage = validationErrors.usernameError
        ) { username = it }

        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField
        UserInputField(
                value = password,
                label = "Password",
                isPassword = true,
                errorMessage = validationErrors.passwordError,
                onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password TextField
        UserInputField(
                value = confirmPassword,
                label = "Confirm Password",
                isPassword = true,
                errorMessage = validationErrors.confirmPasswordError,
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
                    val errors =
                            validateInputs(
                                    firstName,
                                    lastName,
                                    email,
                                    username,
                                    password,
                                    confirmPassword
                            )
                    validationErrors = errors

                    if (errors == ValidationErrors()) { // If no errors
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
        errorMessage: String? = null,
        onValueChange: (String) -> Unit
) {
    Log.d("Lifecycle", "Entering UserInputField Composable")
    Column {
        TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                visualTransformation =
                        if (isPassword) PasswordVisualTransformation()
                        else VisualTransformation.None,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
        )
        if (errorMessage != null) {
            Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

private fun validateInputs(
        firstName: String,
        lastName: String,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
): ValidationErrors {
    return ValidationErrors(
            firstNameError = if (firstName.isBlank()) "First name is required" else null,
            lastNameError = if (lastName.isBlank()) "Last name is required" else null,
            emailError =
                    if (email.isBlank()) {
                        "Email is required"
                    } else if (!email.contains("@")) {
                        "Invalid email format"
                    } else null,
            usernameError = if (username.isBlank()) "Username is required" else null,
            passwordError =
                    when {
                        password.isBlank() -> "Password is required"
                        password.length < 6 -> "Password must be at least 6 characters"
                        else -> null
                    },
            confirmPasswordError =
                    when {
                        confirmPassword.isBlank() -> "Please confirm your password"
                        confirmPassword != password -> "Passwords don't match"
                        else -> null
                    }
    )
}
