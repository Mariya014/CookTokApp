package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.SavedRecipe

@Dao
interface SavedRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecipe(savedRecipe: SavedRecipe): Long

    @Query("""
        SELECT recipes.* FROM recipes 
        INNER JOIN saved_recipes ON recipes.id = saved_recipes.recipeId
        WHERE saved_recipes.userId = :userId
    """)
    suspend fun getSavedRecipesByUser(userId: Int): List<com.example.cooktok.data.local.model.Recipe>

    @Query("DELETE FROM saved_recipes WHERE userId = :userId AND recipeId = :recipeId")
    suspend fun removeSavedRecipe(userId: Int, recipeId: Int)
}
