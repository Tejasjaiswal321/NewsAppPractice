package com.example.todoapppractice.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PersonDetails : Screen("person_details/{userId}") {
        fun createRoute(userId: Long) = "person_details/$userId"
    }
}
