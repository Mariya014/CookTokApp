package com.example.cooktok.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.cooktok.R
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.ui.screens.cuisine.CuisineViewModel
import com.example.cooktok.ui.screens.recipe.RecipeViewModel
import com.example.cooktok.ui.theme.PrimaryRedOrange
import com.example.cooktok.ui.theme.Typography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    recipeViewModel: RecipeViewModel = hiltViewModel(),
    cuisineViewModel: CuisineViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        cuisineViewModel.loadCuisines()
    }

    val recipes by recipeViewModel.recipes.collectAsState(initial = emptyList())
    val cuisines by cuisineViewModel.cuisines.collectAsState(initial = emptyList())
    var selectedCuisineId by remember { mutableStateOf<Int?>(null) }

    var showRecipeDialog by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    // Filter recipes based on selected cuisine
    val filteredRecipes = remember(recipes, selectedCuisineId) {
        if (selectedCuisineId == null) {
            recipes
        } else {
            recipes.filter { it.cuisineId == selectedCuisineId }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Welcome to CookTok!",
                style = Typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cuisine Button with icon
        Button(
            onClick = { navController.navigate("cuisine_screen") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryRedOrange,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Manage Cuisines",
                    style = Typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Explore",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Browse by Cuisine",
            style = Typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" filter chip
            FilterChip(
                selected = selectedCuisineId == null,
                onClick = { selectedCuisineId = null },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryRedOrange,
                    selectedLabelColor = Color.White
                )
            )

            // Cuisine chips
            cuisines.forEach { cuisine ->
                FilterChip(
                    selected = selectedCuisineId == cuisine.id,
                    onClick = { selectedCuisineId = cuisine.id },
                    label = { Text(cuisine.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryRedOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recipe Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredRecipes) { recipe ->
                RecipeCard(recipe = recipe, onClick = {
                    selectedRecipe = recipe
                    showRecipeDialog = true
                })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show recipe details dialog
        if (showRecipeDialog && selectedRecipe != null) {
            RecipeDetailsDialog(
                recipe = selectedRecipe!!,
                onDismiss = { showRecipeDialog = false }
            )
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray)
            ) {
                recipe.imageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(recipe.imageUri),
                        contentDescription = "Recipe Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                } ?: run {
                    Icon(
                        painter = painterResource(R.drawable.recipe_placeholder),
                        contentDescription = "No Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        tint = Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = recipe.title,
                    style = Typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${recipe.cookingTime} min • ${recipe.difficulty}",
                    style = Typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsDialog(
    recipe: Recipe,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        properties = DialogProperties(), content = {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Recipe Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray)
                    ) {
                        recipe.imageUri?.let { uri ->
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(uri)
                                        .build()
                                ),
                                contentDescription = "Recipe Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run {
                            Icon(
                                painter = painterResource(R.drawable.recipe_placeholder),
                                contentDescription = "No Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recipe Title
                    Text(
                        text = recipe.title,
                        style = Typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Basic Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱️ ${recipe.cookingTime} min",
                            style = Typography.bodyMedium
                        )
                        Text(
                            text = "⚡ ${recipe.difficulty}",
                            style = Typography.bodyMedium
                        )

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ingredients
                    Text(
                        text = "Tags",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.tags,
                        style = Typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ingredients
                    Text(
                        text = "Ingredients",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.ingredients,
                        style = Typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Instructions
                    Text(
                        text = "Instructions",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.steps,
                        style = Typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Close Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryRedOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Close")
                    }
                }
            }
        })
}