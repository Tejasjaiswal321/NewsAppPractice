package com.example.todoapppractice.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.todoapppractice.data.db.entity.ExpenseEntity
import com.example.todoapppractice.data.db.entity.ExpenseParticipantEntity
import com.example.todoapppractice.data.db.relation.ExpenseWithParticipants
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Insert
    suspend fun insertParticipants(participants: List<ExpenseParticipantEntity>)

    @Query("DELETE FROM expenses WHERE expense_id = :expenseId")
    suspend fun deleteExpense(expenseId: Long)

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY created_at DESC")
    fun getAllExpensesWithParticipantsFlow(): Flow<List<ExpenseWithParticipants>>

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY created_at DESC")
    suspend fun getAllExpensesWithParticipants(): List<ExpenseWithParticipants>

    // ── Balance aggregation queries ──

    /**
     * Total amount each user has PAID across all expenses.
     * Returns pairs of (user_id, total_paid).
     */
    @Query(
        """
        SELECT paid_by_user_id AS userId, SUM(total_amount) AS totalAmount
        FROM expenses
        GROUP BY paid_by_user_id
    """
    )
    suspend fun getTotalPaidPerUser(): List<UserAmountTuple>

    /**
     * Total amount each user OWES across all expense participations.
     * Returns pairs of (user_id, total_owed).
     */
    @Query(
        """
        SELECT participant_user_id AS userId, SUM(owed_amount) AS totalAmount
        FROM expense_participants
        GROUP BY participant_user_id
    """
    )
    suspend fun getTotalOwedPerUser(): List<UserAmountTuple>

    /**
     * Flow version: emits whenever expenses or participants change.
     */
    @Query(
        """
        SELECT paid_by_user_id AS userId, SUM(total_amount) AS totalAmount
        FROM expenses
        GROUP BY paid_by_user_id
    """
    )
    fun getTotalPaidPerUserFlow(): Flow<List<UserAmountTuple>>

    @Query(
        """
        SELECT participant_user_id AS userId, SUM(owed_amount) AS totalAmount
        FROM expense_participants
        GROUP BY participant_user_id
    """
    )
    fun getTotalOwedPerUserFlow(): Flow<List<UserAmountTuple>>

    // ── Per-person queries ──

    /**
     * Get all expenses where a specific user is the payer.
     */
    @Transaction
    @Query("SELECT * FROM expenses WHERE paid_by_user_id = :userId ORDER BY created_at DESC")
    suspend fun getExpensesPaidByUser(userId: Long): List<ExpenseWithParticipants>

    /**
     * Get all expense_participant rows for a specific user.
     */
    @Query(
        """
        SELECT ep.expense_id, e.paid_by_user_id, ep.owed_amount
        FROM expense_participants ep
        INNER JOIN expenses e ON e.expense_id = ep.expense_id
        WHERE ep.participant_user_id = :userId
    """
    )
    suspend fun getParticipationsForUser(userId: Long): List<ParticipationTuple>
}

data class UserAmountTuple(
    val userId: Long,
    val totalAmount: Long
)

data class ParticipationTuple(
    val expense_id: Long,
    val paid_by_user_id: Long,
    val owed_amount: Long
)
