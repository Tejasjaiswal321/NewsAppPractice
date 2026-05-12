package com.example.todoapppractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.todoapppractice.ui.NewsUI
import com.example.todoapppractice.ui.theme.TodoAppPracticeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppPracticeTheme {
                Scaffold(Modifier.fillMaxSize()) { innerPadding ->
                    NewsUI()
                }
            }
        }
    }
}



