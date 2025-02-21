package com.example.secondnature.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.secondnature.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    Log.d("Lifecycle", "Entering ProfileScreen Composable")
    val viewModel: ProfileViewModel = viewModel()
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState()

    var isEditing by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            firstName = it.firstName
            lastName = it.lastName
            username = it.username
        }
    }

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        error?.let {
            Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (!isEditing) {
            user?.let { user ->
                Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                        text = "${user.firstName} ${user.lastName}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(text = user.email, style = MaterialTheme.typography.bodyMedium)

                IconButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.padding(top = 16.dp)
                ) { Icon(Icons.Default.Edit, contentDescription = "Edit Profile") }
            }
        } else {
            OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                        onClick = {
                            isEditing = false
                            viewModel.updateProfile(firstName, lastName, username)
                        }
                ) { Text("Save") }

                OutlinedButton(
                        onClick = {
                            isEditing = false
                            user?.let {
                                firstName = it.firstName
                                lastName = it.lastName
                                username = it.username
                            }
                        }
                ) { Text("Cancel") }
            }
        }

        Button(
                onClick = {
                    viewModel.signOut()
                    navController.navigate("login") { popUpTo("mainScreen") { inclusive = true } }
                }
        ) { Text("Sign Out") }
    }
}

// no preview since screen uses viewmodel
