package com.example.todoapppractice.domain.model

/**
 * Represents a user's net balance across all expenses and settlements.
 * Positive = others owe this user (creditor).
 * Negative = this user owes others (debtor).
 *
 * @param userId     Room user_id
 * @param displayName  human-readable name
 * @param balancePaise net balance in paise (amount * 100)
 */
data class UserBalance(
    val userId: Long,
    val displayName: String,
    val balancePaise: Long
) {
    /** Formatted balance string like "+703.32" or "-351.66" */
    val formattedBalance: String
        get() {
            val rupees = balancePaise / 100.0
            val sign = if (rupees >= 0) "+" else ""
            return "$sign%.2f".format(rupees)
        }

    val isPositive: Boolean get() = balancePaise > 0
    val isNegative: Boolean get() = balancePaise < 0
    val isSettled: Boolean get() = balancePaise == 0L
}
