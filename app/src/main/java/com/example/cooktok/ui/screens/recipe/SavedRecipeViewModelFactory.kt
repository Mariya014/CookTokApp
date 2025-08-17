package com.example.cooktok.ui.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cooktok.data.repository.SavedRecipeRepository

class SavedRecipeViewModelFactory(
    private val repository: SavedRecipeRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavedRecipeViewModel::class.java)) {
            return SavedRecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}