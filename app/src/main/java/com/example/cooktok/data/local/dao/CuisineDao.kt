package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.Cuisine

@Dao
interface CuisineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuisine(cuisine: Cuisine): Long

    @Query("SELECT * FROM cuisines")
    suspend fun getAllCuisines(): List<Cuisine>
}
