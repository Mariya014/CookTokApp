package com.example.cooktok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import androidx.navigation.compose.*
import com.example.cooktok.data.local.AppDatabase
import com.example.cooktok.data.repository.UserRepository
import com.example.cooktok.ui.navigation.*
import com.example.cooktok.ui.screens.auth.AuthViewModel
import com.example.cooktok.ui.screens.auth.AuthViewModelFactory
import com.example.cooktok.ui.screens.auth.LoginScreen
import com.example.cooktok.ui.screens.auth.SignupScreen
import com.example.cooktok.ui.theme.CookTokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "cooktok_db"
        ).build()

        val userRepository = UserRepository(db.userDao())
        val authFactory = AuthViewModelFactory(userRepository)

        setContent {
            CookTokTheme {
                // Create the AuthViewModel at the root level
                val authViewModel: AuthViewModel = viewModel(factory = authFactory)
                val navController = rememberNavController()

                // Observe the auth state - no need for derivedStateOf here
                val currentUser by authViewModel.currentUser.collectAsState()
                val isLoggedIn = currentUser != null

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) NavRoutes.Main.route else NavRoutes.Login.route
                ) {
                    composable(NavRoutes.Login.route) {
                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate(NavRoutes.Main.route) {
                                    popUpTo(NavRoutes.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToSignup = {
                                navController.navigate(NavRoutes.Signup.route)
                            }
                        )
                    }

                    composable(NavRoutes.Signup.route) {
                        SignupScreen(
                            authViewModel = authViewModel,
                            onSignupSuccess = {
                                navController.navigate(NavRoutes.Main.route) {
                                    popUpTo(NavRoutes.Signup.route) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(NavRoutes.Main.route) {
                        MainScreen(navController = navController, authViewModel = authViewModel)
                    }
                }
            }
        }
    }
}