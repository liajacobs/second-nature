package com.example.secondnature.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.secondnature.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    Log.d("Lifecycle", "Entering ProfileScreen Composable")
    val viewModel: ProfileViewModel = viewModel()
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.error.observeAsState()
    val userPosts by viewModel.userPosts.observeAsState(emptyList())

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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Profile info section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                user?.let { user ->


                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(text = "${user.firstName} ${user.lastName}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = user.email, style = MaterialTheme.typography.bodyMedium)

                    // Edit button
                    IconButton(
                        onClick = { isEditing = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }

                if (isEditing) {
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
                        Button(onClick = { isEditing = false; viewModel.updateProfile(firstName, lastName, username) }) {
                            Text("Save")
                        }

                        OutlinedButton(onClick = {
                            isEditing = false
                            user?.let {
                                firstName = it.firstName
                                lastName = it.lastName
                                username = it.username
                            }
                        }) {
                            Text("Cancel")
                        }
                    }
                }
            }

            // LazyVerticalGrid beneath the pencil icon
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.Start // Aligns it beneath the icon on the left
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(userPosts ?: emptyList()) { post ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .clickable { navController.navigate("post/${post.postId}") }
                                .aspectRatio(3 / 4f)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(post.imageURL),
                                contentDescription = "Post Image",
                                modifier = Modifier.fillMaxSize()
                                    .clip(RoundedCornerShape(20.dp))

                            )
                        }
                    }
                }
            }
        }
    }
}