package com.example.cooktok.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cooktok.data.local.dao.CuisineDao
import com.example.cooktok.data.local.dao.MealPlanDao
import com.example.cooktok.data.local.dao.RecipeDao
import com.example.cooktok.data.local.dao.SavedRecipeDao
import com.example.cooktok.data.local.dao.UserDao
import com.example.cooktok.data.local.model.Cuisine
import com.example.cooktok.data.local.model.MealPlan
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.local.model.SavedRecipe
import com.example.cooktok.data.local.model.User

@Database(entities = [User::class, Cuisine::class, Recipe::class, MealPlan::class, SavedRecipe::class], version =5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cuisineDao(): CuisineDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun savedRecipeDao(): SavedRecipeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cooktok_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
