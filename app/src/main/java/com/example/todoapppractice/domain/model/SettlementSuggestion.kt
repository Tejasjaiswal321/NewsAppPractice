package com.example.todoapppractice.domain.model

/**
 * A computed suggestion for settling debt between two users.
 * Generated at runtime by the simplify algorithm; not yet persisted.
 */
data class SettlementSuggestion(
    val fromUserId: Long,
    val fromName: String,
    val toUserId: Long,
    val toName: String,
    val amountPaise: Long
) {
    val formattedAmount: String
        get() = "%.2f".format(amountPaise / 100.0)
}
