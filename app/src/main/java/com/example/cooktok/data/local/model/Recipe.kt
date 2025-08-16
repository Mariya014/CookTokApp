package com.example.cooktok.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val imageUri: String?,
    val title: String,
    val description: String,
    val cookingTime: Int,
    val difficulty: String,
    val cuisineId: Int?,
    val ingredients: String,
    val steps: String,
    val tags: String // comma-separated
)
