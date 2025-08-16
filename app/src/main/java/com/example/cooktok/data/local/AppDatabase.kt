package com.example.cooktok.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cooktok.data.local.dao.CuisineDao
import com.example.cooktok.data.local.dao.RecipeDao
import com.example.cooktok.data.local.dao.UserDao
import com.example.cooktok.data.local.model.Cuisine
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.local.model.User

@Database(entities = [User::class, Cuisine::class, Recipe::class], version =3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun cuisineDao(): CuisineDao
    abstract fun recipeDao(): RecipeDao

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
