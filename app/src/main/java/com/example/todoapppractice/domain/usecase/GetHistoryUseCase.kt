package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.db.entity.SettlementEntity
import com.example.todoapppractice.data.db.relation.ExpenseWithParticipants
import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.data.repository.ExpenseRepository
import com.example.todoapppractice.data.repository.UserRepository
import com.example.todoapppractice.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Provides a unified, time-sorted history of expenses and settlements.
 */
class GetHistoryUseCase(
    private val expenseRepository: ExpenseRepository,
    private val balanceRepository: BalanceRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<HistoryItem>> = combine(
        expenseRepository.getExpenseHistoryFlow(),
        balanceRepository.getAllSettlementsFlow()
    ) { expenses, settlements ->
        buildHistory(expenses, settlements)
    }

    private suspend fun buildHistory(
        expenses: List<ExpenseWithParticipants>,
        settlements: List<SettlementEntity>
    ): List<HistoryItem> {
        // Cache user lookups
        val userCache = mutableMapOf<Long, String>()
        suspend fun getUserName(userId: Long): String {
            return userCache.getOrPut(userId) {
                userRepository.getUserById(userId)?.displayName ?: "Unknown"
            }
        }

        val expenseItems = expenses.map { ewp ->
            HistoryItem.ExpenseHistoryItem(
                expenseId = ewp.expense.expenseId,
                title = ewp.expense.title,
                totalAmountPaise = ewp.expense.totalAmount,
                paidByName = getUserName(ewp.expense.paidByUserId),
                participantNames = ewp.participants.map { getUserName(it.participantUserId) },
                createdAt = ewp.expense.createdAt
            )
        }

        val settlementItems = settlements.map { s ->
            HistoryItem.SettlementHistoryItem(
                settlementId = s.settlementId,
                fromName = getUserName(s.fromUserId),
                toName = getUserName(s.toUserId),
                amountPaise = s.amount,
                createdAt = s.createdAt
            )
        }

        return (expenseItems + settlementItems)
            .sortedByDescending { it.createdAt }
    }
}
