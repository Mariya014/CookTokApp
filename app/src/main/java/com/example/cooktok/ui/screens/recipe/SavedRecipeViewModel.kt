package com.example.cooktok.ui.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.local.model.SavedRecipe
import com.example.cooktok.data.repository.SavedRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SavedRecipeViewModel(private val repository: SavedRecipeRepository) : ViewModel() {
    private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val savedRecipes: StateFlow<List<Recipe>> = _savedRecipes

    fun loadSavedRecipes(userId: Int) {
        viewModelScope.launch {
            repository.getSavedRecipes(userId).collect {
                _savedRecipes.value = it
            }
        }
    }

    fun saveRecipe(userId: Int, recipeId: Int) {
        viewModelScope.launch {
            repository.saveRecipe(userId, recipeId)
        }
    }

    fun deleteRecipe(savedRecipe: SavedRecipe) {
        viewModelScope.launch {
            repository.deleteSavedRecipe(savedRecipe)
        }
    }
}
