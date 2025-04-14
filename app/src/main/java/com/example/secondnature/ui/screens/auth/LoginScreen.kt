package com.example.secondnature.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.data.repository.AuthRepository
import com.example.secondnature.viewmodel.AuthViewModelFactory
import com.example.secondnature.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit
) {
    Log.d("Lifecycle", "Entering LoginScreen Composable")

    val loginViewModel: LoginViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )


    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Create a coroutine scope for this composable
    val coroutineScope = rememberCoroutineScope()

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Second",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Nature",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                color = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Identifier TextField
        TextField(
                value = identifier,
                onValueChange = { identifier = it },
                label = { Text("Email or Username") },
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
                        val loginSuccessful = loginViewModel.login(identifier, password)

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
