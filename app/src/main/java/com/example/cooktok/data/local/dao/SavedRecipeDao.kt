package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.local.model.SavedRecipe
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipe(savedRecipe: SavedRecipe)

    @Delete
    suspend fun deleteSavedRecipe(savedRecipe: SavedRecipe)

    @Query("SELECT r.* FROM recipes r INNER JOIN saved_recipes s ON r.id = s.recipeId WHERE s.userId = :userId")
    fun getSavedRecipes(userId: Int): Flow<List<Recipe>>
}
