package com.example.todoapppractice.data.repository

import com.example.todoapppractice.data.db.dao.ExpenseDao
import com.example.todoapppractice.data.db.entity.ExpenseEntity
import com.example.todoapppractice.data.db.entity.ExpenseParticipantEntity
import com.example.todoapppractice.data.db.relation.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    /**
     * Insert an expense with its participants in a transaction-safe manner.
     * @param title expense title
     * @param totalAmountPaise total amount in paise
     * @param paidByUserId who paid
     * @param participantShares list of (userId, owedAmountPaise)
     * @return the new expense ID
     */
    suspend fun addExpense(
        title: String,
        totalAmountPaise: Long,
        paidByUserId: Long,
        participantShares: List<Pair<Long, Long>>,
        createdAt: Long = System.currentTimeMillis()
    ): Long {
        val expenseId = expenseDao.insertExpense(
            ExpenseEntity(
                title = title,
                totalAmount = totalAmountPaise,
                paidByUserId = paidByUserId,
                createdAt = createdAt
            )
        )

        val participantEntities = participantShares.map { (userId, owedAmount) ->
            ExpenseParticipantEntity(
                expenseId = expenseId,
                participantUserId = userId,
                owedAmount = owedAmount
            )
        }
        expenseDao.insertParticipants(participantEntities)

        return expenseId
    }

    suspend fun deleteExpense(expenseId: Long) {
        expenseDao.deleteExpense(expenseId)
    }

    fun getExpenseHistoryFlow(): Flow<List<ExpenseWithParticipants>> =
        expenseDao.getAllExpensesWithParticipantsFlow()

    suspend fun getAllExpensesWithParticipants(): List<ExpenseWithParticipants> =
        expenseDao.getAllExpensesWithParticipants()
}
