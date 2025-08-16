package com.example.cooktok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                val navController = rememberNavController()
                var isLoggedIn by remember { mutableStateOf(false) }

                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) NavRoutes.Main.route else NavRoutes.Login.route
                ) {

                    composable(NavRoutes.Login.route) {
                        val authViewModel: AuthViewModel = viewModel(factory = authFactory)

                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                isLoggedIn = true
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
                        val authViewModel: AuthViewModel = viewModel(factory = authFactory)

                        SignupScreen(
                            authViewModel = authViewModel,
                            onSignupSuccess = {
                                isLoggedIn = true
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
                        MainScreen(navController = navController)
                    }
                }
            }
        }
    }
}
