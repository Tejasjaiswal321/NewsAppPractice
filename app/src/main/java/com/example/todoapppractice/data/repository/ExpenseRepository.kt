package com.example.todoapppractice.data.repository

import com.example.todoapppractice.data.db.dao.ExpenseDao
import com.example.todoapppractice.data.db.dao.ParticipationTuple
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
        val expense = ExpenseEntity(
            title = title,
            totalAmount = totalAmountPaise,
            paidByUserId = paidByUserId,
            createdAt = createdAt
        )

        // expenseId=0 is a placeholder; the DAO's @Transaction method
        // replaces it with the real auto-generated ID before inserting.
        val participantEntities = participantShares.map { (userId, owedAmount) ->
            ExpenseParticipantEntity(
                expenseId = 0,
                participantUserId = userId,
                owedAmount = owedAmount
            )
        }

        return expenseDao.insertExpenseWithParticipants(expense, participantEntities)
    }

    suspend fun deleteExpense(expenseId: Long) {
        expenseDao.deleteExpense(expenseId)
    }

    fun getExpenseHistoryFlow(): Flow<List<ExpenseWithParticipants>> =
        expenseDao.getAllExpensesWithParticipantsFlow()


    /** Get all expenses paid by a specific user (with their participants). */
    suspend fun getExpensesPaidByUser(userId: Long): List<ExpenseWithParticipants> =
        expenseDao.getExpensesPaidByUser(userId)

    /** Get all expense participations for a specific user (filtered, not full table). */
    suspend fun getParticipationsForUser(userId: Long): List<ParticipationTuple> =
        expenseDao.getParticipationsForUser(userId)
}
