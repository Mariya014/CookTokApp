package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.Recipe

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Query("SELECT * FROM recipes WHERE userId = :userId")
    suspend fun getRecipesByUser(userId: Int): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): Recipe?

    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>
}
