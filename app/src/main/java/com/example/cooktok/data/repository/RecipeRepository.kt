package com.example.cooktok.data.repository

import com.example.cooktok.data.local.dao.RecipeDao
import com.example.cooktok.data.local.model.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {
    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    suspend fun getRecipeById(id: Int) = recipeDao.getRecipeById(id)
    fun getRecipesByCuisine(cuisineId: Int) = recipeDao.getRecipesByCuisine(cuisineId)
}
