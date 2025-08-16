package com.example.cooktok.data.repository

import com.example.cooktok.data.local.dao.UserDao
import com.example.cooktok.data.local.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun signup(user: User): Long {
        return userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}
