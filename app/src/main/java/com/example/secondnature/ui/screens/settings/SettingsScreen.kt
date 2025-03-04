package com.example.secondnature.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.navigate("mainScreen") {
                            popUpTo("mainScreen") { inclusive = false }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sign Out Button
            Button(
                onClick = {
                    viewModel.signOut()
                    navController.navigate("login") { 
                        popUpTo("mainScreen") { inclusive = true } 
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Sign Out")
            }
            
            // Delete Account Button
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Delete Account")
            }
        }
    }
    
    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteDialog = false
                password = ""
                passwordError = null
            },
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text("This action cannot be undone. Please enter your password to confirm.")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        isError = passwordError != null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    
                    passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                        } else {
                            viewModel.deleteAccount(password,
                                onSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("mainScreen") { inclusive = true }
                                    }
                                },
                                onError = { errorMessage ->
                                    passwordError = errorMessage
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { 
                    showDeleteDialog = false
                    password = ""
                    passwordError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
} 