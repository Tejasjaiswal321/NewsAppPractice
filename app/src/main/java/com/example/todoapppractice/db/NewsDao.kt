package com.example.todoapppractice.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.todoapppractice.ui.data.Article

@Dao
interface NewsDao {
    @Upsert
    fun saveList(list: List<Article>)

    @Query("select * from news_table")
    fun getNewsList(): List<Article>

    @Query("delete from news_table")
    fun cleanDB()

    @Transaction
    fun updateList(list: List<Article>) {
        cleanDB()
        saveList(list)
    }
}