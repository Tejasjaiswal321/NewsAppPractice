package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.domain.model.PersonSummary

/**
 * Get a specific person's balance summary and their individual
 * settlement suggestions (who owes them / who they owe).
 */
class GetPersonSummaryUseCase(
    private val balanceRepository: BalanceRepository
) {
    suspend operator fun invoke(userId: Long): PersonSummary? {
        val allBalances = balanceRepository.getBalances()
        val person = allBalances.find { it.userId == userId } ?: return null

        // Generate settlements from current balances
        val allSettlements = SimplifyBalancesUseCase.generateSettlements(allBalances)

        // Filter settlements involving this person
        val relevantSettlements = allSettlements.filter {
            it.fromUserId == userId || it.toUserId == userId
        }

        return PersonSummary(
            userId = person.userId,
            displayName = person.displayName,
            netBalancePaise = person.balancePaise,
            settlements = relevantSettlements
        )
    }
}
