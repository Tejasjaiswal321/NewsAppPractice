package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.data.repository.ExpenseRepository
import com.example.todoapppractice.data.repository.UserRepository
import com.example.todoapppractice.domain.model.PersonSummary
import com.example.todoapppractice.domain.model.SettlementSuggestion

/**
 * Get a specific person's balance personSummary and their individual
 * settlement suggestions (who owes them / who they owe).
 *
 * Supports two modes:
 * - Simplified (isSimplified=true): greedy algorithm on NET balances (minimal transactions)
 * - Raw (isSimplified=false): actual pairwise debts from individual expenses + existing settlements
 */
class GetPersonSummaryUseCase(
    private val balanceRepository: BalanceRepository,
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, isSimplified: Boolean): PersonSummary? {
        val allBalances = balanceRepository.getBalances()
        val person = allBalances.find { it.userId == userId } ?: return null

        val settlements = if (isSimplified) {
            getSimplifiedSettlements(userId)
        } else {
            getRawPairwiseDebts(userId)
        }

        return PersonSummary(
            userId = person.userId,
            displayName = person.displayName,
            netBalancePaise = person.balancePaise,
            settlements = settlements
        )
    }

    /**
     * Simplified mode: run greedy algorithm on net balances.
     * Shows minimal number of transactions to settle everything.
     * For a net-zero user like C, this returns empty → "All settled up!"
     */
    private suspend fun getSimplifiedSettlements(userId: Long): List<SettlementSuggestion> {
        val allBalances = balanceRepository.getBalances()
        val allSettlements = SimplifyBalancesUseCase.generateSettlements(allBalances)
        return allSettlements.filter {
            it.fromUserId == userId || it.toUserId == userId
        }
    }

    /**
     * Raw mode: compute actual pairwise debts from individual expenses.
     *
     * For each expense:
     *   - If user X was the payer: each other participant owes X their share
     *   - If user X was a participant (not payer): X owes the payer their share
     *
     * Then subtract any existing settlement entries from the DB
     * (from individual "Settle" button clicks) to show remaining debts.
     */
    private suspend fun getRawPairwiseDebts(userId: Long): List<SettlementSuggestion> {
        val allExpenses = expenseRepository.getAllExpensesWithParticipants()

        // pairwiseDebts: otherUserId → net amount
        // Positive = other owes me, Negative = I owe other
        val pairwiseDebts = mutableMapOf<Long, Long>()

        for (ewp in allExpenses) {
            val expense = ewp.expense
            val participants = ewp.participants

            if (expense.paidByUserId == userId) {
                // I paid → each other participant owes me their share
                for (p in participants) {
                    if (p.participantUserId != userId) {
                        pairwiseDebts[p.participantUserId] =
                            (pairwiseDebts[p.participantUserId] ?: 0L) + p.owedAmount
                    }
                }
            } else {
                // Someone else paid → check if I'm a participant
                for (p in participants) {
                    if (p.participantUserId == userId) {
                        pairwiseDebts[expense.paidByUserId] =
                            (pairwiseDebts[expense.paidByUserId] ?: 0L) - p.owedAmount
                    }
                }
            }
        }

        // Subtract existing settlements from DB (individual "Settle" clicks)
        val existingSettlements = balanceRepository.getAllSettlements()
        for (s in existingSettlements) {
            if (s.fromUserId == userId) {
                // I paid someone → my debt to them decreases (my balance with them goes up)
                pairwiseDebts[s.toUserId] =
                    (pairwiseDebts[s.toUserId] ?: 0L) + s.amount
            } else if (s.toUserId == userId) {
                // Someone paid me → their debt to me decreases (my balance with them goes down)
                pairwiseDebts[s.fromUserId] =
                    (pairwiseDebts[s.fromUserId] ?: 0L) - s.amount
            }
        }

        // Convert to SettlementSuggestion list, filtering out zero-balance pairs
        val myName = userRepository.getUserById(userId)?.displayName ?: "Unknown"

        return pairwiseDebts.mapNotNull { (otherUserId, amount) ->
            if (amount == 0L) return@mapNotNull null

            val otherName = userRepository.getUserById(otherUserId)?.displayName ?: "Unknown"

            if (amount > 0) {
                // Other owes me → they should pay me
                SettlementSuggestion(
                    fromUserId = otherUserId,
                    fromName = otherName,
                    toUserId = userId,
                    toName = myName,
                    amountPaise = amount
                )
            } else {
                // I owe other → I should pay them
                SettlementSuggestion(
                    fromUserId = userId,
                    fromName = myName,
                    toUserId = otherUserId,
                    toName = otherName,
                    amountPaise = -amount
                )
            }
        }.sortedByDescending { it.amountPaise }
    }
}
