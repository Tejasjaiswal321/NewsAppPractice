package com.example.todoapppractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.todoapppractice.ui.SplitWiseApp
import com.example.todoapppractice.ui.screen.SparkleScreen
import com.example.todoapppractice.ui.theme.TodoAppPracticeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppPracticeTheme {
//                SplitWiseApp()
                SparkleScreen()
            }
        }
    }
}
