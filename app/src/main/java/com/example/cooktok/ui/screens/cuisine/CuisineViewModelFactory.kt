package com.example.cooktok.ui.screens.cuisine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cooktok.data.repository.CuisineRepository

class CuisineViewModelFactory(private val repository: CuisineRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CuisineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CuisineViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
