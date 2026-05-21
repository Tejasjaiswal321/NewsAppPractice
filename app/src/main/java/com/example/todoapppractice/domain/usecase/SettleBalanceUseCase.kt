package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.db.entity.SettlementEntity
import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.domain.model.SettlementSuggestion

/**
 * Settle a specific balance between two users.
 * Inserts a settlement entry into the append-only ledger.
 */
class SettleBalanceUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(suggestion: SettlementSuggestion): Long {
        return balanceRepository.insertSettlement(
            SettlementEntity(
                fromUserId = suggestion.fromUserId,
                toUserId = suggestion.toUserId,
                amount = suggestion.amountPaise,
                createdAt = System.currentTimeMillis()
            )
        )
    }
}
