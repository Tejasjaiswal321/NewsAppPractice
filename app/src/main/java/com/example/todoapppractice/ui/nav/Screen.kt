package com.example.todoapppractice.ui.nav


sealed class Screen(val route: String) {
    object List : Screen("list")
    object Details : Screen("details")
}