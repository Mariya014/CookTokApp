package com.example.cooktok.ui.screens.recipe

import androidx.lifecycle.*
import com.example.cooktok.data.local.model.Recipe
import com.example.cooktok.data.repository.RecipeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    // Recipes list
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // Operation states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _addRecipeSuccess = MutableStateFlow(false)
    val addRecipeSuccess: StateFlow<Boolean> = _addRecipeSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            repository.getAllRecipes()
                .catch { e ->
                    _errorMessage.value = e.message ?: "Failed to load recipes"
                }
                .collect { recipes ->
                    _recipes.value = recipes
                }
        }
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.insertRecipe(recipe)
                _addRecipeSuccess.value = true
                loadRecipes() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add recipe"
                _addRecipeSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.updateRecipe(recipe)
                loadRecipes() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update recipe"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                repository.deleteRecipe(recipe)
                loadRecipes() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete recipe"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAddRecipeState() {
        _addRecipeSuccess.value = false
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

class RecipeViewModelFactory(private val repository: RecipeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}