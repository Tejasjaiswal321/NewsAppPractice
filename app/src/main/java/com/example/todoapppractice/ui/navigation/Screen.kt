package com.example.todoapppractice.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object PersonDetails : Screen("person_details/{userId}") {
        fun createRoute(userId: Long) = "person_details/$userId"
    }
}
