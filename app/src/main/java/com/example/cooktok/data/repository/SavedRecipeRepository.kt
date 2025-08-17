package com.example.cooktok.data.repository

import com.example.cooktok.data.local.dao.SavedRecipeDao
import com.example.cooktok.data.local.model.SavedRecipe

class SavedRecipeRepository(private val dao: SavedRecipeDao) {
    fun getSavedRecipes(userId: Int) = dao.getSavedRecipes(userId)
    suspend fun saveRecipe(userId: Int, recipeId: Int) =
        dao.saveRecipe(SavedRecipe(userId = userId, recipeId = recipeId))
    suspend fun unsaveRecipe(userId: Int, recipeId: Int) {
        dao.deleteSavedRecipe(userId, recipeId)
    }
}
