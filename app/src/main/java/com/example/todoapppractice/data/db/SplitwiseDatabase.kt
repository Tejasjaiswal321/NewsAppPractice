package com.example.todoapppractice.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todoapppractice.data.db.dao.ExpenseDao
import com.example.todoapppractice.data.db.dao.SettlementDao
import com.example.todoapppractice.data.db.dao.UserDao
import com.example.todoapppractice.data.db.entity.ExpenseEntity
import com.example.todoapppractice.data.db.entity.ExpenseParticipantEntity
import com.example.todoapppractice.data.db.entity.SettlementEntity
import com.example.todoapppractice.data.db.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        ExpenseEntity::class,
        ExpenseParticipantEntity::class,
        SettlementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SplitwiseDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settlementDao(): SettlementDao
}
