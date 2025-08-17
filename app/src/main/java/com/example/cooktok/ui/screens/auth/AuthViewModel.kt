package com.example.cooktok.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooktok.data.local.model.User
import com.example.cooktok.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    init {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            _currentUser.value = user
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter both email and password"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val user = userRepository.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                } else {
                    _errorMessage.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Login failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(displayName: String, email: String, password: String) {
        if (displayName.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill all fields"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val newUser = User(displayName = displayName, email = email, password = password)
                val id = userRepository.signup(newUser)

                if (id > 0) {
                    _currentUser.value = newUser.copy(id = id.toInt())
                } else {
                    _errorMessage.value = "Failed to create account"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Signup failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}