package com.example.cooktok.ui.screens.recipe

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.ui.screens.auth.AuthViewModel
import com.example.cooktok.ui.screens.cuisine.CuisineViewModel
import com.example.cooktok.ui.theme.PrimaryRedOrange
import com.example.cooktok.utils.persistImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddRecipeScreen(
    recipeViewModel: RecipeViewModel = hiltViewModel(),
    cuisineViewModel: CuisineViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    authViewModel: AuthViewModel,

    ) {

    val currentUser by authViewModel.currentUser.collectAsState()

    // Form state
    var recipeTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var cookingTime by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf("") }
    var selectedCuisineId by remember { mutableStateOf<Int?>(null) }
    var ingredients by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var recipeImageUri by remember { mutableStateOf<String?>(null) }

    // UI state
    var showSuccessDialog by remember { mutableStateOf(false) }
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val addRecipeSuccess by recipeViewModel.addRecipeSuccess.collectAsState()
    val errorMessage by recipeViewModel.errorMessage.collectAsState()

    // Fetch cuisines
    val cuisines by cuisineViewModel.cuisines.collectAsState(initial = emptyList())

// Context for saving image
    val context = LocalContext.current

// Image picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Save the picked image permanently and get file path
            val savedPath = persistImage(context, it)
            recipeImageUri = savedPath
        }
    }



    // Difficulties
    val difficulties = listOf("Easy", "Medium", "Hard")

    // Handle success state
    LaunchedEffect(addRecipeSuccess) {
        if (addRecipeSuccess) {
            showSuccessDialog = true
            // Clear form
            recipeTitle = ""
            description = ""
            cookingTime = ""
            selectedDifficulty = ""
            selectedCuisineId = null
            ingredients = ""
            steps = ""
            tags = ""
            recipeImageUri = null
            recipeViewModel.resetAddRecipeState()
        }
    }

    if (errorMessage != null) {
        AlertDialog(
            onDismissRequest = { recipeViewModel.clearError() },
            title = { Text("Error") },
            text = { Text(errorMessage!!) },
            confirmButton = {
                Button(
                    onClick = { recipeViewModel.clearError() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRedOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onNavigateBack()
            },
            title = { Text("Success!") },
            text = { Text("Recipe uploaded successfully") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRedOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PrimaryRedOrange,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (recipeImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(recipeImageUri)
                                    .build()
                            ),
                            contentDescription = "Recipe Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(48.dp),
                                tint = PrimaryRedOrange
                            )
                            Text(
                                text = "Add Photo",
                                color = PrimaryRedOrange
                            )
                        }
                    }
                }
            }

            item {
                // Basic Information Section
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipeTitle,
                    onValueChange = { recipeTitle = it },
                    label = { Text("Recipe Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = cookingTime,
                    onValueChange = { cookingTime = it },
                    label = { Text("Cooking Time (minutes) *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )
            }

            item {
                // Difficulty Section
                Text(
                    text = "Difficulty *",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    difficulties.forEach { difficulty ->
                        FilterChip(
                            selected = selectedDifficulty == difficulty,
                            onClick = { selectedDifficulty = difficulty },
                            label = { Text(difficulty) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryRedOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                // Cuisine Section (now using FilterChips like Difficulty)
                Text(
                    text = "Cuisine *",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (cuisines.isEmpty()) {
                    Text(
                        text = "No cuisines available",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    // Use FlowRow for better wrapping of chips
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
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
                }
            }
            item {
                // Ingredients Section
                Text(
                    text = "Ingredients *",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
                    placeholder = { Text("Enter ingredients separated by commas") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )
            }

            item {
                // Cooking Steps Section
                Text(
                    text = "Cooking Steps *",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = steps,
                    onValueChange = { steps = it },
                    placeholder = { Text("Enter each step on a new line") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )
            }

            item {
                // Tags Section
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    placeholder = { Text("e.g., quick, healthy, vegetarian") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryRedOrange,
                        focusedLabelColor = PrimaryRedOrange
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                // Submit Button
                Button(
                    onClick = {
                        val newRecipe = Recipe(
                            userId = currentUser?.id ?: 0,
                            imageUri = recipeImageUri,
                            title = recipeTitle,
                            description = description,
                            cookingTime = cookingTime.toIntOrNull() ?: 0,
                            difficulty = selectedDifficulty,
                            cuisineId = selectedCuisineId,
                            ingredients = ingredients,
                            steps = steps,
                            tags = tags
                        )
                        recipeViewModel.addRecipe(newRecipe)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryRedOrange,
                        contentColor = Color.White
                    ),
                    enabled = !isLoading &&
                            recipeTitle.isNotEmpty() &&
                            description.isNotEmpty() &&
                            cookingTime.isNotEmpty() &&
                            selectedDifficulty.isNotEmpty() &&
                            selectedCuisineId != null &&
                            ingredients.isNotEmpty() &&
                            steps.isNotEmpty()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            "UPLOAD RECIPE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
