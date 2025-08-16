package com.example.cooktok.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cooktok.ui.screens.auth.AuthViewModel
import com.example.cooktok.ui.screens.auth.LoginScreen
import com.example.cooktok.ui.screens.auth.SignupScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Home : Screen("home") // later will hold bottom nav
}

@Composable
fun NavGraph(navController: NavHostController, authViewModel: AuthViewModel, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onNavigateToSignup = { navController.navigate(Screen.Signup.route) }
            )
        }
        composable(Screen.Signup.route) {
            SignupScreen(
                authViewModel = authViewModel,
                onSignupSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        // Later: composable(Screen.Home.route) { BottomNavGraph() }
    }
}
