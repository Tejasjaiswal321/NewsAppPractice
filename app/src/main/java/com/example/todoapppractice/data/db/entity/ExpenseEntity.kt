package com.example.todoapppractice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["paid_by_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["paid_by_user_id"])]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    val expenseId: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "total_amount")
    val totalAmount: Long,  // stored in paise (amount * 100)

    @ColumnInfo(name = "paid_by_user_id")
    val paidByUserId: Long,

    @ColumnInfo(name = "created_at")
    val createdAt: Long  // epoch millis
)
