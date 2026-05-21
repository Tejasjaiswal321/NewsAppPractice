package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.domain.model.UserBalance
import kotlinx.coroutines.flow.Flow

/**
 * Provides a reactive Flow of all user balances.
 * Balances are computed dynamically from the expense + settlement ledger.
 */
class GetBalancesUseCase(
    private val balanceRepository: BalanceRepository
) {
    operator fun invoke(): Flow<List<UserBalance>> =
        balanceRepository.getBalancesFlow()
}
