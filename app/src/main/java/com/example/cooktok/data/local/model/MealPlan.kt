package com.example.cooktok.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,            // if you plan to support multi-user
    val date: String,           // store in "yyyy-MM-dd"
    val mealType: String,       // "breakfast", "lunch", "dinner", "snack"
    val recipeId: Int           // foreign key to Recipe
)
