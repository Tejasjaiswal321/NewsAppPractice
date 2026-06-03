package com.example.todoapppractice.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_participants",
    foreignKeys = [
        ForeignKey(
            entity = ExpenseEntity::class,
            parentColumns = ["expense_id"],
            childColumns = ["expense_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["participant_user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["expense_id"]),
        Index(value = ["participant_user_id"])
    ]
)
data class ExpenseParticipantEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_participant_id")
    val expenseParticipantId: Long = 0,

    @ColumnInfo(name = "expense_id")
    val expenseId: Long = 0,

    @ColumnInfo(name = "participant_user_id")
    val participantUserId: Long,

    @ColumnInfo(name = "owed_amount")
    val owedAmount: Long  // stored in paise
)
