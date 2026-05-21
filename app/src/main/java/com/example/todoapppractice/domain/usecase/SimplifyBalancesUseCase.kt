package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.db.entity.SettlementEntity
import com.example.todoapppractice.data.repository.BalanceRepository
import com.example.todoapppractice.domain.model.SettlementSuggestion
import com.example.todoapppractice.domain.model.UserBalance

/**
 * Simplify all outstanding balances into minimal settlement transactions.
 *
 * Algorithm:
 * 1. Compute net balances from ledger
 * 2. Exact-match cancellation (optimization)
 * 3. Greedy two-pointer settlement on remaining
 * 4. Persist settlement rows (append-only, never mutate expenses)
 */
class SimplifyBalancesUseCase(
    private val balanceRepository: BalanceRepository
) {

    /**
     * Compute settlement suggestions without persisting.
     */
    suspend fun computeSuggestions(): List<SettlementSuggestion> {
        val balances = balanceRepository.getBalances()
        return generateSettlements(balances)
    }

    /**
     * Persist the simplification as settlement entries.
     * Returns number of settlements created.
     */
    suspend fun execute(): Int {
        val suggestions = computeSuggestions()
        if (suggestions.isEmpty()) return 0

        val now = System.currentTimeMillis()
        val entities = suggestions.map { s ->
            SettlementEntity(
                fromUserId = s.fromUserId,
                toUserId = s.toUserId,
                amount = s.amountPaise,
                createdAt = now
            )
        }
        balanceRepository.insertSettlements(entities)
        return entities.size
    }

    companion object {

        /**
         * Generate minimal settlement suggestions from current balances.
         * Uses exact-match cancellation + greedy algorithm.
         */
        fun generateSettlements(balances: List<UserBalance>): List<SettlementSuggestion> {
            // Filter out settled users
            val nonZero = balances.filter { !it.isSettled }.toMutableList()
            if (nonZero.isEmpty()) return emptyList()

            val results = mutableListOf<SettlementSuggestion>()

            // ── Phase 1: Exact-match cancellation ──
            val creditors = nonZero.filter { it.isPositive }.toMutableList()
            val debtors = nonZero.filter { it.isNegative }.toMutableList()

            // HashMap by absolute balance for O(1) exact-match lookup
            val creditorsByAmount = HashMap<Long, MutableList<UserBalance>>()
            for (c in creditors) {
                creditorsByAmount.getOrPut(c.balancePaise) { mutableListOf() }.add(c)
            }

            val matchedDebtors = mutableSetOf<Long>()  // userId
            val matchedCreditors = mutableSetOf<Long>() // userId

            for (debtor in debtors) {
                val absAmount = -debtor.balancePaise  // positive
                val matches = creditorsByAmount[absAmount]
                if (matches != null && matches.isNotEmpty()) {
                    val creditor = matches.removeFirst()
                    if (matches.isEmpty()) creditorsByAmount.remove(absAmount)

                    results.add(
                        SettlementSuggestion(
                            fromUserId = debtor.userId,
                            fromName = debtor.displayName,
                            toUserId = creditor.userId,
                            toName = creditor.displayName,
                            amountPaise = absAmount
                        )
                    )
                    matchedDebtors.add(debtor.userId)
                    matchedCreditors.add(creditor.userId)
                }
            }

            // ── Phase 2: Greedy two-pointer on remaining ──
            data class MutableBalance(
                val userId: Long,
                val name: String,
                var amount: Long
            )

            val remainingCreditors = creditors
                .filter { it.userId !in matchedCreditors }
                .map { MutableBalance(it.userId, it.displayName, it.balancePaise) }
                .sortedByDescending { it.amount }
                .toMutableList()

            val remainingDebtors = debtors
                .filter { it.userId !in matchedDebtors }
                .map {
                    MutableBalance(
                        it.userId,
                        it.displayName,
                        -it.balancePaise
                    )
                } // store as positive
                .sortedByDescending { it.amount }
                .toMutableList()

            var ci = 0
            var di = 0
            while (ci < remainingCreditors.size && di < remainingDebtors.size) {
                val creditor = remainingCreditors[ci]
                val debtor = remainingDebtors[di]

                val settleAmount = minOf(creditor.amount, debtor.amount)
                if (settleAmount > 0) {
                    results.add(
                        SettlementSuggestion(
                            fromUserId = debtor.userId,
                            fromName = debtor.name,
                            toUserId = creditor.userId,
                            toName = creditor.name,
                            amountPaise = settleAmount
                        )
                    )
                }

                creditor.amount -= settleAmount
                debtor.amount -= settleAmount

                if (creditor.amount == 0L) ci++
                if (debtor.amount == 0L) di++
            }

            return results
        }
    }
}
