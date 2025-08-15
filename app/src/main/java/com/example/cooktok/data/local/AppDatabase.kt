package com.example.cooktok.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cooktok.data.local.dao.*
import com.example.cooktok.data.local.model.*

@Database(
    entities = [User::class, Recipe::class, Cuisine::class, SavedRecipe::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recipeDao(): RecipeDao
    abstract fun cuisineDao(): CuisineDao
    abstract fun savedRecipeDao(): SavedRecipeDao
}
