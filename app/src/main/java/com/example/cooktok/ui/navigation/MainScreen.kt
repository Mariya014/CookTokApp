package com.example.cooktok.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.cooktok.data.local.AppDatabase
import com.example.cooktok.data.repository.CuisineRepository
import com.example.cooktok.data.repository.RecipeRepository
import com.example.cooktok.ui.screens.cuisine.CuisineScreen
import com.example.cooktok.ui.screens.cuisine.CuisineViewModel
import com.example.cooktok.ui.screens.cuisine.CuisineViewModelFactory
import com.example.cooktok.ui.screens.home.HomeScreen
import com.example.cooktok.ui.screens.recipe.AddRecipeScreen
import com.example.cooktok.ui.screens.recipe.RecipeViewModel
import com.example.cooktok.ui.screens.recipe.RecipeViewModelFactory

@Composable
fun MainScreen(navController: NavHostController) {
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.AddRecipe,
        BottomNavItem.MealPlanner,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(navController = bottomNavController)
            }

            composable(BottomNavItem.AddRecipe.route) {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val recipeRepository = remember { RecipeRepository(db.recipeDao()) }
                val recipeViewModel: RecipeViewModel = viewModel(
                    factory = RecipeViewModelFactory(recipeRepository)
                )

                val cuisineRepository = remember { CuisineRepository(db.cuisineDao()) }
                val cuisineViewModel: CuisineViewModel = viewModel(
                    factory = CuisineViewModelFactory(cuisineRepository)
                )

                AddRecipeScreen(
                    recipeViewModel = recipeViewModel,
                    cuisineViewModel = cuisineViewModel,
                    onNavigateBack = { bottomNavController.navigateUp() }
                )
            }

            composable(BottomNavItem.MealPlanner.route) { Text("📅 Meal Planner Screen") }
            composable(BottomNavItem.Profile.route) { Text("👤 Profile Screen") }

            composable("cuisine_screen") {
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val cuisineRepository = remember { CuisineRepository(db.cuisineDao()) }
                val cuisineViewModel: CuisineViewModel = viewModel(
                    factory = CuisineViewModelFactory(cuisineRepository)
                )

                CuisineScreen(viewModel = cuisineViewModel)
            }
        }
    }
}