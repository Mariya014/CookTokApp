package com.example.cooktok.data.local.model
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_recipes")
data class SavedRecipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val recipeId: Int
)
