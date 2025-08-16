package com.example.cooktok.ui.screens.cuisine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooktok.data.local.model.Cuisine
import com.example.cooktok.data.repository.CuisineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CuisineViewModel(private val repository: CuisineRepository) : ViewModel() {

    private val _cuisines = MutableStateFlow<List<Cuisine>>(emptyList())
    val cuisines: StateFlow<List<Cuisine>> = _cuisines

    init {
        loadCuisines()
    }

    fun loadCuisines() {
        viewModelScope.launch {
            _cuisines.value = repository.getAllCuisines()
        }
    }

    fun addCuisine(name: String) {
        viewModelScope.launch {
            repository.insertCuisine(Cuisine(name = name))
            loadCuisines()
        }
    }

    fun updateCuisine(cuisine: Cuisine) {
        viewModelScope.launch {
            repository.updateCuisine(cuisine)
            loadCuisines()
        }
    }

    fun deleteCuisine(cuisine: Cuisine) {
        viewModelScope.launch {
            repository.deleteCuisine(cuisine)
            loadCuisines()
        }
    }
}
