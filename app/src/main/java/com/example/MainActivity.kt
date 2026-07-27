package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.remote.SessionManager
import com.example.data.repository.AsuliaRepository
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.RechargeScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.AsuliaTechTheme
import com.example.ui.viewmodel.AlertsViewModel
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.DashboardViewModel
import com.example.ui.viewmodel.HistoryViewModel
import com.example.ui.viewmodel.ProfileViewModel
import com.example.ui.viewmodel.RechargeViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize SessionManager for API auth persistence
    SessionManager.init(applicationContext)

    setContent {
      AsuliaTechTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AsuliaTechApp()
        }
      }
    }
  }
}

@Composable
fun AsuliaTechApp() {
  val navController = rememberNavController()

  // Initialize shared repository & viewmodels
  val repository = remember { AsuliaRepository() }
  val authViewModel = remember { AuthViewModel(repository) }
  val dashboardViewModel = remember { DashboardViewModel(repository) }
  val historyViewModel = remember { HistoryViewModel(repository) }
  val alertsViewModel = remember { AlertsViewModel(repository) }
  val profileViewModel = remember { ProfileViewModel(repository) }
  val rechargeViewModel = remember { RechargeViewModel(repository) }

  NavHost(
    navController = navController,
    startDestination = "splash",
    enterTransition = { fadeIn(animationSpec = tween(300)) },
    exitTransition = { fadeOut(animationSpec = tween(300)) }
  ) {
    composable("splash") {
      SplashScreen(
        onSplashFinished = {
          val destination = if (SessionManager.isLoggedIn()) "main" else "login"
          navController.navigate(destination) {
            popUpTo("splash") { inclusive = true }
          }
        }
      )
    }

    composable("login") {
      LoginScreen(
        authViewModel = authViewModel,
        onLoginSuccess = {
          navController.navigate("main") {
            popUpTo("login") { inclusive = true }
          }
        }
      )
    }

    composable("main") {
      MainScreen(
        dashboardViewModel = dashboardViewModel,
        historyViewModel = historyViewModel,
        alertsViewModel = alertsViewModel,
        profileViewModel = profileViewModel,
        authViewModel = authViewModel,
        onNavigateToRecharge = {
          navController.navigate("recharge")
        },
        onLogout = {
          navController.navigate("login") {
            popUpTo("main") { inclusive = true }
          }
        }
      )
    }

    composable(
      route = "recharge",
      enterTransition = {
        slideIntoContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Up,
          animationSpec = tween(400)
        )
      },
      exitTransition = {
        slideOutOfContainer(
          towards = AnimatedContentTransitionScope.SlideDirection.Down,
          animationSpec = tween(400)
        )
      }
    ) {
      RechargeScreen(
        rechargeViewModel = rechargeViewModel,
        onNavigateBack = {
          navController.popBackStack()
        }
      )
    }
  }
}

