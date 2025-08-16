package com.example.cooktok.data.repository

import androidx.lifecycle.LiveData
import com.example.cooktok.data.local.dao.MealPlanDao
import com.example.cooktok.data.local.model.MealPlan

class MealPlanRepository(private val mealPlanDao: MealPlanDao) {

    suspend fun insertMealPlan(mealPlan: MealPlan) {
        mealPlanDao.insertMealPlan(mealPlan)
    }

    suspend fun updateMealPlan(mealPlan: MealPlan) {
        mealPlanDao.updateMealPlan(mealPlan)
    }

    suspend fun deleteMealPlan(mealPlan: MealPlan) {
        mealPlanDao.deleteMealPlan(mealPlan)
    }

    fun getMealPlansForDate(date: String, userId: Int): LiveData<List<MealPlan>> {
        return mealPlanDao.getMealPlansForDate(date, userId)
    }

    fun getAllMealPlans(userId: Int): LiveData<List<MealPlan>> {
        return mealPlanDao.getAllMealPlans(userId)
    }
}
