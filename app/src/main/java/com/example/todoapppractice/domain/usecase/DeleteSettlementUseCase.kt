package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.BalanceRepository

/**
 * Deletes a settlement by ID.
 * Balances auto-update via Flow.
 */
class DeleteSettlementUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(settlementId: Long) {
        balanceRepository.deleteSettlement(settlementId)
    }
}
