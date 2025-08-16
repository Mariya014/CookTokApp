package com.example.cooktok.ui.screens.mealPlan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cooktok.data.local.AppDatabase
import com.example.cooktok.data.local.model.MealPlan
import com.example.cooktok.data.repository.MealPlanRepository
import com.example.cooktok.ui.screens.recipe.RecipeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    navController: NavController,
    userId: Int = 1, // Default user ID, replace with actual user management
    mealViewModel: MealPlanViewModel = hiltViewModel(),

    ) {
    val currentDate = LocalDate.now()
    val weekDates = remember { getWeekDates(currentDate) }
    var selectedDate by remember { mutableStateOf(currentDate) }


    // Get meal plans for the week
    val mealPlans by mealViewModel.getAllMealPlans(userId).observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Planner") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Open shopping list */ }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping List")
                        Badge(modifier = Modifier.offset((-8).dp, 8.dp)) {
                            Text("0") // Update with actual count
                        }
                    }
                }
            )
        },
//        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Week selector
            WeekSelector(weekDates, selectedDate) { date ->
                selectedDate = date
            }

            // Daily meal plan
            DailyMealPlan(
                date = selectedDate,
                mealPlans = mealPlans.filter { it.date == selectedDate.format(DateTimeFormatter.ISO_DATE) },
                onAddMeal = { mealType ->
                    // Navigate to recipe selection
                    navController.navigate("recipeSelection/$userId/${selectedDate.format(DateTimeFormatter.ISO_DATE)}/$mealType")
                },
                onMealClick = { mealPlan ->
                    // Navigate to recipe details
                    navController.navigate("recipeDetails/${mealPlan.recipeId}")
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekSelector(
    weekDates: List<LocalDate>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDates.forEach { date ->
            val isSelected = date == selectedDate
            Column(
                modifier = Modifier
                    .clickable { onDateSelected(date) }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.dayOfWeek.toString().take(3),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyMealPlan(
    date: LocalDate,
    mealPlans: List<MealPlan>,
    onAddMeal: (String) -> Unit,
    onMealClick: (MealPlan) -> Unit
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(mealTypes) { mealType ->
            val mealsForType = mealPlans.filter { it.mealType.equals(mealType, ignoreCase = true) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mealType,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = { onAddMeal(mealType) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add $mealType"
                            )
                        }
                    }

                    if (mealsForType.isEmpty()) {
                        Text(
                            text = "No $mealType planned",
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            mealsForType.forEach { mealPlan ->
                                MealItem(mealPlan, onMealClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MealItem(mealPlan: MealPlan, onClick: (MealPlan) -> Unit) {
    // Replace with actual recipe name fetched from repository
    val recipeName = "Recipe #${mealPlan.recipeId}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(mealPlan) },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for recipe image
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = recipeName,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getWeekDates(date: LocalDate): List<LocalDate> {
    val startOfWeek = date.minusDays(date.dayOfWeek.value.toLong() - 1)
    return (0 until 7).map { startOfWeek.plusDays(it.toLong()) }
}