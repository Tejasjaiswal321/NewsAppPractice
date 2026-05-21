package com.example.todoapppractice.domain.usecase

import com.example.todoapppractice.data.repository.ExpenseRepository
import com.example.todoapppractice.data.repository.UserRepository

/**
 * Orchestrates adding an expense:
 * 1. Validates inputs
 * 2. Normalizes & deduplicates participant names
 * 3. Gets or creates User entities
 * 4. Calculates per-participant shares (handling remainder)
 * 5. Inserts expense + participant rows
 */
class AddExpenseUseCase(
    private val expenseRepository: ExpenseRepository,
    private val userRepository: UserRepository
) {

    sealed interface Result {
        data class Success(val expenseId: Long) : Result
        data class Error(val message: String) : Result
    }

    suspend operator fun invoke(
        title: String,
        totalAmountRupees: Int,
        paidByName: String,
        participantNames: List<String>
    ): Result {

        // ── Validation ──

        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return Result.Error("Expense name cannot be empty")
        }

        if (totalAmountRupees <= 0) {
            return Result.Error("Total must be greater than 0")
        }

        val trimmedPaidBy = paidByName.trim()
        if (trimmedPaidBy.isBlank()) {
            return Result.Error("Paid by cannot be empty")
        }

        // Deduplicate participants (case-insensitive)
        val normalizedPaidBy = userRepository.normalizeName(trimmedPaidBy)

        val allParticipantNames = buildList {
            // Payer is always a participant
            add(trimmedPaidBy)
            for (name in participantNames) {
                val trimmed = name.trim()
                if (trimmed.isBlank()) continue
                val normalized = userRepository.normalizeName(trimmed)
                // Skip if duplicate of payer or already added
                if (normalized != normalizedPaidBy &&
                    none { userRepository.normalizeName(it) == normalized }
                ) {
                    add(trimmed)
                }
            }
        }

        if (allParticipantNames.size < 2) {
            return Result.Error("Need at least 2 participants (including payer)")
        }

        // ── Get or create users ──

        val paidByUserId = userRepository.getOrCreateUser(trimmedPaidBy)
        val participantUserIds = allParticipantNames.map { name ->
            userRepository.getOrCreateUser(name)
        }

        // ── Calculate shares ──
        // Store in paise to avoid floating point
        val totalPaise = totalAmountRupees.toLong() * 100
        val count = allParticipantNames.size
        val baseShare = totalPaise / count
        val remainder = totalPaise % count  // distribute 1 paise to first N participants

        val shares = participantUserIds.mapIndexed { index, userId ->
            val extra = if (index < remainder) 1L else 0L
            userId to (baseShare + extra)
        }

        // ── Insert ──

        val expenseId = expenseRepository.addExpense(
            title = trimmedTitle,
            totalAmountPaise = totalPaise,
            paidByUserId = paidByUserId,
            participantShares = shares
        )

        return Result.Success(expenseId)
    }
}
