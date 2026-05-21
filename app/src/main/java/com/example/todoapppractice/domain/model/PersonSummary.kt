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
)
