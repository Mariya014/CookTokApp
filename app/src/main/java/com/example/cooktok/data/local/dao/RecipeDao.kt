package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Query("SELECT * FROM recipes ORDER BY id DESC")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Int): Recipe?

    @Query("SELECT * FROM recipes WHERE cuisineId = :cuisineId")
    fun getRecipesByCuisine(cuisineId: Int): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE userId = :userId")
    fun getRecipesByUser(userId: Int): Flow<List<Recipe>>
}
