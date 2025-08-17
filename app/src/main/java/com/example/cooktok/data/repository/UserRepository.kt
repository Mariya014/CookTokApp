package com.example.cooktok.data.repository

import com.example.cooktok.data.local.dao.UserDao
import com.example.cooktok.data.local.model.User

class UserRepository(private val userDao: UserDao) {
    private var currentUser: User? = null

    suspend fun signup(user: User): Long {
        val id = userDao.insertUser(user)
        if (id > 0) {
            currentUser = user.copy(id = id.toInt())
        }
        return id
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)?.also {
            currentUser = it
        }
    }

    suspend fun getCurrentUser(): User? {
        return currentUser ?: userDao.getUserById(currentUser?.id ?: -1)?.also {
            currentUser = it
        }
    }

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
}