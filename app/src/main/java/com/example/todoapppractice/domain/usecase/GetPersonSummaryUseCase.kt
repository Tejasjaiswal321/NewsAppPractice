package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.data.repository.ExpenseRepository
import com.example.todoapppractice.data.repository.UserRepository
import com.example.todoapppractice.domain.model.PersonSummary
import com.example.todoapppractice.domain.model.SettlementSuggestion

/**
 * Get a specific person's balance summary and their individual
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
     * Uses per-user filtered DAO queries (not full table scan) and
     * pre-fetches user names into a map to avoid N+1 queries.
     *
     * For each expense:
     *   - If this user was the payer: each other participant owes them their share
     *   - If this user was a participant (not payer): they owe the payer their share
     *
     * Then subtract any existing settlement entries from the DB
     * (from individual "Settle" button clicks) to show remaining debts.
     */
    private suspend fun getRawPairwiseDebts(userId: Long): List<SettlementSuggestion> {
        // Pre-fetch all users into a map — eliminates N+1 getUserById() calls
        val usersById = userRepository.getAllUsersById()
        val myName = usersById[userId]?.displayName ?: "Unknown"

        // pairwiseDebts: otherUserId → net amount
        // Positive = other owes me, Negative = I owe other
        val pairwiseDebts = mutableMapOf<Long, Long>()

        // 1) Expenses I paid: each other participant owes me their share
        val expensesPaidByMe = expenseRepository.getExpensesPaidByUser(userId)
        for (ewp in expensesPaidByMe) {
            for (p in ewp.participants) {
                if (p.participantUserId != userId) {
                    pairwiseDebts[p.participantUserId] =
                        (pairwiseDebts[p.participantUserId] ?: 0L) + p.owedAmount
                }
            }
        }

        // 2) Expenses where I'm a participant (someone else paid): I owe the payer
        val myParticipations = expenseRepository.getParticipationsForUser(userId)
        for (p in myParticipations) {
            if (p.paid_by_user_id != userId) {
                pairwiseDebts[p.paid_by_user_id] =
                    (pairwiseDebts[p.paid_by_user_id] ?: 0L) - p.owed_amount
            }
        }

        // 3) Subtract existing settlements from DB (individual "Settle" clicks)
        val existingSettlements = balanceRepository.getAllSettlements()
        for (s in existingSettlements) {
            if (s.fromUserId == userId) {
                // I paid someone → my debt to them decreases
                pairwiseDebts[s.toUserId] =
                    (pairwiseDebts[s.toUserId] ?: 0L) + s.amount
            } else if (s.toUserId == userId) {
                // Someone paid me → their debt to me decreases
                pairwiseDebts[s.fromUserId] =
                    (pairwiseDebts[s.fromUserId] ?: 0L) - s.amount
            }
        }

        // Convert to SettlementSuggestion list, filtering out zero-balance pairs
        return pairwiseDebts.mapNotNull { (otherUserId, amount) ->
            if (amount == 0L) return@mapNotNull null

            val otherName = usersById[otherUserId]?.displayName ?: "Unknown"

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
