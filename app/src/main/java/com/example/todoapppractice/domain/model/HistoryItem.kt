package com.example.todoapppractice.domain.model

/**
 * Unified history item for the History tab.
 * Combines expenses and settlements sorted by timestamp descending.
 */
sealed interface HistoryItem {

    val id: Long
    val createdAt: Long

    data class ExpenseHistoryItem(
        val expenseId: Long,
        val title: String,
        val totalAmountPaise: Long,
        val paidByName: String,
        val participantNames: List<String>,
        override val createdAt: Long
    ) : HistoryItem {
        override val id: Long get() = expenseId
    }

    data class SettlementHistoryItem(
        val settlementId: Long,
        val fromName: String,
        val toName: String,
        val amountPaise: Long,
        override val createdAt: Long
    ) : HistoryItem {
        override val id: Long get() = settlementId
    }
}
