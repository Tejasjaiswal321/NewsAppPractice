package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.ExpenseRepository

/**
 * Deletes an expense by ID.
 * Cascade delete removes associated participant rows.
 * Balances auto-update via Flow.
 */
class DeleteExpenseUseCase(
    private val expenseRepository: ExpenseRepository
) {
    suspend operator fun invoke(expenseId: Long) {
        expenseRepository.deleteExpense(expenseId)
    }
}
