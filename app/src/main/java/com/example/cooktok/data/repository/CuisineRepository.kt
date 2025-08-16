package com.example.cooktok.data.repository

import com.example.cooktok.data.local.dao.CuisineDao
import com.example.cooktok.data.local.model.Cuisine

class CuisineRepository(private val cuisineDao: CuisineDao) {

    suspend fun getAllCuisines(): List<Cuisine> {
        return cuisineDao.getAllCuisines()
    }

    suspend fun insertCuisine(cuisine: Cuisine) {
        cuisineDao.insertCuisine(cuisine)
    }

    suspend fun updateCuisine(cuisine: Cuisine) {
        cuisineDao.updateCuisine(cuisine)
    }

    suspend fun deleteCuisine(cuisine: Cuisine) {
        cuisineDao.deleteCuisine(cuisine)
    }
}
