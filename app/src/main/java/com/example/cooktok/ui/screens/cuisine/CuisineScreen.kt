package com.example.cooktok.ui.screens.cuisine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cooktok.data.local.model.Cuisine
import com.example.cooktok.ui.theme.PrimaryRedOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuisineScreen(viewModel: CuisineViewModel) {
    val cuisines by viewModel.cuisines.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editCuisine by remember { mutableStateOf<Cuisine?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Cuisines",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryRedOrange,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryRedOrange,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Cuisine",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            if (cuisines.isEmpty()) {
                EmptyStateUI()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(cuisines) { cuisine ->
                        CuisineItem(
                            cuisine = cuisine,
                            onEditClick = {
                                editCuisine = cuisine
                                showEditDialog = true
                            },
                            onDeleteClick = { viewModel.deleteCuisine(cuisine) }
                        )
                    }
                }
            }
        }

        // Add Cuisine Dialog
        if (showAddDialog) {
            AddEditCuisineDialog(
                title = "Add New Cuisine",
                initialName = "",
                onDismiss = { showAddDialog = false },
                onConfirm = { name ->
                    if (name.isNotBlank()) {
                        viewModel.addCuisine(name)
                        showAddDialog = false
                    }
                }
            )
        }

        // Edit Cuisine Dialog
        if (showEditDialog && editCuisine != null) {
            AddEditCuisineDialog(
                title = "Edit Cuisine",
                initialName = editCuisine!!.name,
                onDismiss = { showEditDialog = false },
                onConfirm = { name ->
                    if (name.isNotBlank()) {
                        viewModel.updateCuisine(editCuisine!!.copy(name = name))
                        showEditDialog = false
                    }
                }
            )
        }
    }
}

@Composable
private fun CuisineItem(
    cuisine: Cuisine,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = cuisine.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = PrimaryRedOrange
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateUI() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🍜",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "No Cuisines Yet",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Add your first cuisine by tapping the + button below",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AddEditCuisineDialog(
    title: String,
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var cuisineName by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            OutlinedTextField(
                value = cuisineName,
                onValueChange = { cuisineName = it },
                label = { Text("Cuisine Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                    focusedLabelColor = PrimaryRedOrange,
                    focusedIndicatorColor = PrimaryRedOrange
                ),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(cuisineName) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryRedOrange,
                    contentColor = Color.White
                ),
                enabled = cuisineName.isNotBlank(),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = if (initialName.isEmpty()) "Add" else "Update",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}