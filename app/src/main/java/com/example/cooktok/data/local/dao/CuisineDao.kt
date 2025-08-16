package com.example.cooktok.data.local.dao

import androidx.room.*
import com.example.cooktok.data.local.model.Cuisine

@Dao
interface CuisineDao {

    @Query("SELECT * FROM cuisines")
    suspend fun getAllCuisines(): List<Cuisine>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuisine(cuisine: Cuisine)

    @Update
    suspend fun updateCuisine(cuisine: Cuisine)

    @Delete
    suspend fun deleteCuisine(cuisine: Cuisine)
}
