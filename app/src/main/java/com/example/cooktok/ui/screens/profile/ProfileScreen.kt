package com.example.cooktok.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.cooktok.R
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.local.model.SavedRecipe
import com.example.cooktok.ui.navigation.NavRoutes
import com.example.cooktok.ui.navigation.Screen
import com.example.cooktok.ui.screens.auth.AuthViewModel
import com.example.cooktok.ui.screens.recipe.RecipeViewModel
import com.example.cooktok.ui.screens.recipe.SavedRecipeViewModel
import com.example.cooktok.ui.theme.PrimaryRedOrange
import com.example.cooktok.ui.theme.Typography

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    recipeViewModel: RecipeViewModel,
    savedRecipeViewModel: SavedRecipeViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val myRecipes by recipeViewModel.userRecipes.collectAsState()
    val savedRecipes by savedRecipeViewModel.savedRecipes.collectAsState()

    // Handle recipe deletion confirmation
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    // State for dialogs
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var recipeToModify by remember { mutableStateOf<Recipe?>(null) }
    var showSignOutDialog by remember { mutableStateOf(false) }


    // Load data when screen appears
    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            recipeViewModel.loadUserRecipes(user.id)
            savedRecipeViewModel.loadSavedRecipes(user.id)
        }
    }

    if (showDeleteDialog && recipeToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete this recipe?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipeToDelete?.let { recipe ->
                            recipeViewModel.deleteRecipe(recipe)
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog && recipeToModify != null) {
        EditRecipeDialog(
            recipe = recipeToModify!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRecipe ->
                recipeViewModel.updateRecipe(updatedRecipe)
                showEditDialog = false
            }
        )
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.logout()
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                        }
                        showSignOutDialog = false
                    }
                ) {
                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Image(
                painter = painterResource(R.drawable.profile_placeholder),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = currentUser?.displayName ?: "User",
                style = Typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            // User Email
            Text(
                text = currentUser?.email ?: "email@example.com",
                style = Typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(count = myRecipes.size.toString(), label = "Recipes")
                ProfileStatItem(count = savedRecipes.size.toString(), label = "Saved")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Out Button
            OutlinedButton(
                onClick = { showSignOutDialog = true },  // Changed from direct logout
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryRedOrange
                )
            ) {
                Text("Sign Out")
            }

        }

        // Divider
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )

        // Content Tabs
        var selectedTab by remember { mutableIntStateOf(0) }
        val tabs = listOf("My Recipes", "Saved")

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = PrimaryRedOrange,
            divider = {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = Typography.titleMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }

        // Content Area
        when (selectedTab) {
            0 -> RecipeListContent(
                recipes = myRecipes,
                emptyMessage = "No recipes yet\nStart sharing your favorite recipes with the community!",
                isSavedTab = false,
                onUploadClick = { navController.navigate("add_recipe") },
                onEditClick = { recipe ->
                    recipeToModify = recipe
                    showEditDialog = true
                },
                onDeleteClick = { recipe ->
                    recipeToDelete = recipe
                    showDeleteDialog = true
                },
                onUnsaveClick = {} // Not used in My Recipes tab
            )
            1 -> RecipeListContent(
                recipes = savedRecipes,
                emptyMessage = "No saved recipes yet\nSave recipes to see them here!",
                isSavedTab = true,
                onUploadClick = { navController.navigate("add_recipe") },
                onEditClick = {}, // Not used in Saved tab
                onDeleteClick = {}, // Not used in Saved tab
                onUnsaveClick = { recipe ->
                    currentUser?.let { user ->
                        savedRecipeViewModel.unsaveRecipe(
                            userId = user.id,
                            recipeId = recipe.id
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun RecipeListContent(
    recipes: List<Recipe>,
    emptyMessage: String,
    isSavedTab: Boolean = false,
    onUploadClick: () -> Unit,
    onEditClick: (Recipe) -> Unit,
    onDeleteClick: (Recipe) -> Unit,
    onUnsaveClick: (Recipe) -> Unit
) {
    if (recipes.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emptyMessage,
                style = Typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Button(
                onClick = onUploadClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryRedOrange,
                    contentColor = Color.White
                )
            ) {
                Text("Upload Recipe")
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeItem(
                    recipe = recipe,
                    isSavedRecipe = isSavedTab,
                    onEditClick = { onEditClick(recipe) },
                    onDeleteClick = { onDeleteClick(recipe) },
                    onUnsaveClick = { onUnsaveClick(recipe) }
                )
            }
        }
    }
}
@Composable
private fun RecipeItem(
    recipe: Recipe,
    isSavedRecipe: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onUnsaveClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(recipe.imageUri ?: R.drawable.recipe_placeholder),
                contentDescription = "Recipe image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = Typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${recipe.cookingTime} min • ${recipe.difficulty}",
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            if (isSavedRecipe) {
                IconButton(
                    onClick = onUnsaveClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Unsave",
                        tint = PrimaryRedOrange
                    )
                }
            } else {
                Row {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun ProfileStatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = count,
            style = Typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        )
        Text(
            text = label,
            style = Typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onSave: (Recipe) -> Unit
) {
    var title by remember { mutableStateOf(recipe.title) }
    var description by remember { mutableStateOf(recipe.description) }
    var cookingTime by remember { mutableStateOf(recipe.cookingTime.toString()) }
    var difficulty by remember { mutableStateOf(recipe.difficulty) }
    var ingredients by remember { mutableStateOf(recipe.ingredients) }
    var steps by remember { mutableStateOf(recipe.steps) }
    var tags by remember { mutableStateOf(recipe.tags) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Edit Recipe",
                    style = Typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cookingTime,
                        onValueChange = { cookingTime = it },
                        label = { Text("Time (min)") },
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = difficulty,
                        onValueChange = { difficulty = it },
                        label = { Text("Difficulty") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    label = { Text("Ingredients (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    label = { Text("Steps") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSave(
                                recipe.copy(
                                    title = title,
                                    description = description,
                                    cookingTime = cookingTime.toIntOrNull() ?: 30,
                                    difficulty = difficulty,
                                    ingredients = ingredients,
                                    steps = steps,
                                    tags = tags
                                )
                            )
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}