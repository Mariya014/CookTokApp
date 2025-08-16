package com.example.cooktok.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuisines")
data class Cuisine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)
