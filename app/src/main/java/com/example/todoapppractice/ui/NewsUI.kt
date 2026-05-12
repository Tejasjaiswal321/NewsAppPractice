package com.example.todoapppractice.ui

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapppractice.ui.components.DetailsScreen
import com.example.todoapppractice.ui.components.ListScreen
import com.example.todoapppractice.ui.nav.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewsUI(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel = koinViewModel<NewsViewModel>()
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(Screen.List.route) {
            ListScreen(viewModel) {
                viewModel.updateSelectedUIItem(it)
                navController.navigate(Screen.Details.route)
                Toast.makeText(
                    context,
                    "You have clicked on news #${it.id}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        composable(
            route = Screen.Details.route,
        ) {
            DetailsScreen(viewModel)
        }
    }
}