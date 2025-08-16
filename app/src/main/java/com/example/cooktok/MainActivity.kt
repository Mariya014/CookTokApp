package com.example.cooktok

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.cooktok.data.local.AppDatabase
import com.example.cooktok.data.repository.UserRepository
import com.example.cooktok.ui.navigation.NavGraph
import com.example.cooktok.ui.screens.auth.AuthViewModel
import com.example.cooktok.ui.theme.CookTokTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Initialize Room Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "cooktok_db"
        ).build()

        // ✅ Inject repository into ViewModel
        val userRepository = UserRepository(db.userDao())
        val authViewModel = AuthViewModel(userRepository)

        setContent {
            CookTokTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, authViewModel = authViewModel)
            }
        }
    }
}
