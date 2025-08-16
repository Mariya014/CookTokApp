package com.example.cooktok.ui.screens.mealPlan


import androidx.lifecycle.*
import com.example.cooktok.data.local.model.MealPlan
import com.example.cooktok.data.repository.MealPlanRepository
import kotlinx.coroutines.launch

class MealPlanViewModel(private val repository: MealPlanRepository) : ViewModel() {

    fun insertMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        repository.insertMealPlan(mealPlan)
    }

    fun updateMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        repository.updateMealPlan(mealPlan)
    }

    fun deleteMealPlan(mealPlan: MealPlan) = viewModelScope.launch {
        repository.deleteMealPlan(mealPlan)
    }

    fun getMealPlansForDate(date: String, userId: Int): LiveData<List<MealPlan>> {
        return repository.getMealPlansForDate(date, userId)
    }

    fun getAllMealPlans(userId: Int): LiveData<List<MealPlan>> {
        return repository.getAllMealPlans(userId)
    }
}

class MealPlanViewModelFactory(private val repository: MealPlanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealPlanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
