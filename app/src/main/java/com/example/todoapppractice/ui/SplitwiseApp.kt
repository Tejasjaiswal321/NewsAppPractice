package com.example.todoapppractice.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapppractice.ui.navigation.Screen
import com.example.todoapppractice.ui.screen.home.HomeScreen
import com.example.todoapppractice.ui.screen.person.PersonDetailScreen
import kotlinx.coroutines.launch

@Composable
fun SplitWiseApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToPerson = { userId ->
                        navController.navigate(Screen.PersonDetails.createRoute(userId))
                    },
                    snackbarHostState = snackbarHostState
                )
            }

            composable(
                route = Screen.PersonDetails.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getLong("userId") ?: -1L
                PersonDetailScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() },
                    onSnackbar = { message ->
                        scope.launch { snackbarHostState.showSnackbar(message) }
                    }
                )
            }
        }
    }
}
