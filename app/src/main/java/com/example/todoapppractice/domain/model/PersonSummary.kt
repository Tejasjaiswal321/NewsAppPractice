package com.example.todoapppractice.domain.model

/**
 * Summary of a specific person's financial position,
 * including who they owe and who owes them.
 */
data class PersonSummary(
    val userId: Long,
    val displayName: String,
    val netBalancePaise: Long,
    val settlements: List<SettlementSuggestion>
) {
    val formattedBalance: String
        get() {
            val rupees = netBalancePaise / 100.0
            val sign = if (rupees >= 0) "+" else ""
            return "$sign%.2f".format(rupees)
        }
}
