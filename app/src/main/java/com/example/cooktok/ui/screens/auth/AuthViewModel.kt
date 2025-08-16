package com.example.cooktok.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooktok.data.local.model.User
import com.example.cooktok.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.login(email, password)
            if (user != null) {
                _currentUser.value = user
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Invalid email or password"
            }
        }
    }

    fun signup(displayName: String, email: String, password: String) {
        viewModelScope.launch {
            val newUser = User(displayName = displayName, email = email, password = password)
            val id = userRepository.signup(newUser)
            if (id > 0) {
                _currentUser.value = newUser.copy(id = id.toInt())
                _errorMessage.value = null
            } else {
                _errorMessage.value = "Failed to create account"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}
