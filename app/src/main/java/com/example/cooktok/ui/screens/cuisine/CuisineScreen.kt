package com.example.cooktok.ui.screens.cuisine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.cooktok.data.local.model.Cuisine

@Composable
fun CuisineScreen(viewModel: CuisineViewModel) {

    val cuisines by viewModel.cuisines.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editCuisine by remember { mutableStateOf<Cuisine?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("+", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Cuisines", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            if (cuisines.isEmpty()) {
                Text("No cuisines yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(cuisines) { cuisine ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cuisine.name, style = MaterialTheme.typography.bodyLarge)

                            Row {
                                IconButton(onClick = {
                                    editCuisine = cuisine
                                    showEditDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }

                                IconButton(onClick = {
                                    viewModel.deleteCuisine(cuisine)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1️⃣ Add Cuisine Dialog
        if (showAddDialog) {
            var newCuisineName by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add Cuisine") },
                text = {
                    OutlinedTextField(
                        value = newCuisineName,
                        onValueChange = { newCuisineName = it },
                        label = { Text("Cuisine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newCuisineName.isNotBlank()) {
                            viewModel.addCuisine(newCuisineName)
                            showAddDialog = false
                        }
                    }) {
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // 2️⃣ Edit Cuisine Dialog
        if (showEditDialog && editCuisine != null) {
            var updatedName by remember { mutableStateOf(editCuisine!!.name) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Cuisine") },
                text = {
                    OutlinedTextField(
                        value = updatedName,
                        onValueChange = { updatedName = it },
                        label = { Text("Cuisine Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (updatedName.isNotBlank()) {
                            viewModel.updateCuisine(editCuisine!!.copy(name = updatedName))
                            showEditDialog = false
                        }
                    }) {
                        Text("Update", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
