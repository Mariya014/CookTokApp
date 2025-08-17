package com.example.cooktok.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object AddRecipe : BottomNavItem("add_recipe", "Add Recipe", Icons.Default.Add)
    object MealPlanner : BottomNavItem("meal_planner", "Meal Planner", Icons.Default.DateRange)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}
