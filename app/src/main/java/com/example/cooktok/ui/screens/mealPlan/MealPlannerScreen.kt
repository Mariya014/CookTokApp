package com.example.cooktok.ui.screens.mealPlan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.cooktok.data.local.model.MealPlan
import com.example.cooktok.data.local.model.Recipe
import java.text.SimpleDateFormat
import java.util.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    mealPlanViewModel: MealPlanViewModel,
    recipes: List<Recipe>,
    userId: Int
) {
    val context = LocalContext.current
    val dbFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val uiFormat = remember { SimpleDateFormat("EEE, MMM d", Locale.getDefault()) }
    val weekDates = remember { currentWeekDates() }
    var dialogFor by remember { mutableStateOf<DialogTarget?>(null) }

    // Color scheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Weekly Meal Plan",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Week navigation header
            WeekNavigationHeader(weekDates, uiFormat)

            // Days list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(weekDates) { dateObj ->
                    val dateKey = dbFormat.format(dateObj)
                    val uiLabel = uiFormat.format(dateObj)
                    val plansForDate by mealPlanViewModel.getMealPlansForDate(dateKey, userId)
                        .observeAsState(emptyList())
                    val byMeal = remember(plansForDate) { plansForDate.associateBy { it.mealType } }

                    DayCard(
                        dateLabel = uiLabel,
                        breakfastTitle = recipeTitle(byMeal["Breakfast"]?.recipeId, recipes),
                        lunchTitle = recipeTitle(byMeal["Lunch"]?.recipeId, recipes),
                        dinnerTitle = recipeTitle(byMeal["Dinner"]?.recipeId, recipes),
                        onBreakfastClick = { dialogFor = DialogTarget(dateKey, "Breakfast") },
                        onLunchClick = { dialogFor = DialogTarget(dateKey, "Lunch") },
                        onDinnerClick = { dialogFor = DialogTarget(dateKey, "Dinner") },
                        primaryColor = primaryColor,
                        surfaceColor = surfaceColor,
                        onSurfaceColor = onSurfaceColor
                    )
                }
            }
        }
    }

    // Recipe selection dialog
    dialogFor?.let { target ->
        RecipeSelectionDialog(
            target = target,
            recipes = recipes,
            onDismiss = { dialogFor = null },
            onRecipeSelected = { recipe ->
                mealPlanViewModel.insertMealPlan(
                    MealPlan(
                        id = 0,
                        userId = userId,
                        date = target.dateKey,
                        mealType = target.mealType,
                        recipeId = recipe.id
                    )
                )
                Toast.makeText(
                    context,
                    "${recipe.title} added to ${target.mealType}",
                    Toast.LENGTH_SHORT
                ).show()
                dialogFor = null
            }
        )
    }
}

@Composable
private fun WeekNavigationHeader(weekDates: List<Date>, dateFormat: SimpleDateFormat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weekDates.forEach { date ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = dateFormat.format(date).take(3), // Short day name
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = SimpleDateFormat("d", Locale.getDefault()).format(date),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun DayCard(
    dateLabel: String,
    breakfastTitle: String?,
    lunchTitle: String?,
    dinnerTitle: String?,
    onBreakfastClick: () -> Unit,
    onLunchClick: () -> Unit,
    onDinnerClick: () -> Unit,
    primaryColor: Color,
    surfaceColor: Color,
    onSurfaceColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = primaryColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MealTimeCard(
                    title = breakfastTitle ?: "Add Breakfast",
                    isAssigned = breakfastTitle != null,
                    onClick = onBreakfastClick,
                    modifier = Modifier.weight(1f),
                    primaryColor = primaryColor
                )
                MealTimeCard(
                    title = lunchTitle ?: "Add Lunch",
                    isAssigned = lunchTitle != null,
                    onClick = onLunchClick,
                    modifier = Modifier.weight(1f),
                    primaryColor = primaryColor
                )
                MealTimeCard(
                    title = dinnerTitle ?: "Add Dinner",
                    isAssigned = dinnerTitle != null,
                    onClick = onDinnerClick,
                    modifier = Modifier.weight(1f),
                    primaryColor = primaryColor
                )
            }
        }
    }
}

@Composable
private fun MealTimeCard(
    title: String,
    isAssigned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryColor: Color
) {
    val containerColor = if (isAssigned) primaryColor.copy(alpha = 0.1f)
    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val textColor = if (isAssigned) primaryColor
    else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isAssigned) primaryColor
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(if (isAssigned) 4.dp else 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isAssigned) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun RecipeSelectionDialog(
    target: DialogTarget,
    recipes: List<Recipe>,
    onDismiss: () -> Unit,
    onRecipeSelected: (Recipe) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Select recipe for ${target.mealType}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            if (recipes.isEmpty()) {
                Text(
                    "No recipes available. Please add recipes first.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recipes) { recipe ->
                        RecipeSelectionItem(
                            recipe = recipe,
                            onClick = { onRecipeSelected(recipe) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

@Composable
private fun RecipeSelectionItem(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Recipe image placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Recipe",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${recipe.cookingTime} min • ${recipe.difficulty}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class DialogTarget(val dateKey: String, val mealType: String)

private fun currentWeekDates(): List<Date> {
    val cal = Calendar.getInstance()
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return (0..6).map { offset ->
        (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset) }.time
    }
}

private fun recipeTitle(recipeId: Int?, recipes: List<Recipe>): String? {
    return recipeId?.let { id -> recipes.firstOrNull { it.id == id }?.title }
}