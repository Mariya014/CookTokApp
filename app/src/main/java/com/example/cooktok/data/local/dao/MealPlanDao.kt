package com.example.cooktok.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.cooktok.data.local.model.MealPlan

@Dao
interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan)

    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)

    @Query("SELECT * FROM meal_plans WHERE date = :date AND userId = :userId")
    fun getMealPlansForDate(date: String, userId: Int): LiveData<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE userId = :userId ORDER BY date ASC")
    fun getAllMealPlans(userId: Int): LiveData<List<MealPlan>>
}
