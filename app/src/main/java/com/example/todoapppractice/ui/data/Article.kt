package com.example.todoapppractice.ui.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class Article(
    @PrimaryKey
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)