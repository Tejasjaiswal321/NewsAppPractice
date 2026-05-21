package com.example.todoapppractice.data.db.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.todoapppractice.data.db.entity.ExpenseEntity
import com.example.todoapppractice.data.db.entity.ExpenseParticipantEntity

data class ExpenseWithParticipants(
    @Embedded
    val expense: ExpenseEntity,

    @Relation(
        parentColumn = "expense_id",
        entityColumn = "expense_id"
    )
    val participants: List<ExpenseParticipantEntity>
)
